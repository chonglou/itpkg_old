__author__ = 'zhengjitang@gmail.com'

import pickle
from brahma.models import Item, LogFlag
from brahma.env import db_call, encrypt as _encrypt


class User(object):
    id = None
    created = None

    def __init__(self, flag, username=None, password=None, email=None, openid=None, token=None):
        self.username = username

        from brahma.env import encrypt

        self.salt = encrypt.random_str(8)

        if flag == "E":
            if username and password and email:
                self.username = username
                self.email = email
                self.password = encrypt.sha512(password + self.salt)
                self.flag = flag
            else:
                raise ValueError()
        elif flag == "G":
            if openid and token:
                self.openid = openid
                self.token = token
                self.flag = flag
            else:
                raise ValueError()
        elif flag == "Q":
            if openid and token:
                self.openid = openid
                self.token = token
                self.flag = flag
                raise ValueError()

    @staticmethod
    def check(plain, salt, password):
        from brahma.env import encrypt

        return encrypt.sha512(plain + salt) == password

    def __repr__(self):
        return "<User('%s', '%s')>" % (self.id, self.username)


class Task(object):
    @staticmethod
    def set_next_run(tid, next_run=None):
        pass

    @staticmethod
    def list_available():
        return list()

    @staticmethod
    def get(tid):
        return tid


class Setting(object):
    @staticmethod
    @db_call
    def startup(flag=True, cnx=None):
        import datetime

        cursor = cnx.cursor()
        Setting._set("site.startup" if flag else "site.shutdown", datetime.datetime.now(), False, cursor=cursor)
        cursor = cnx.cursor()
        Log._log(message="启动系统" if flag else "关闭系统", user=None, flag=LogFlag.INFO, cursor=cursor)


    @staticmethod
    @db_call
    def get(key, encrypt=False, cnx=None):
        return Setting._get(key, encrypt, cnx.cursor())

    @staticmethod
    @db_call
    def set(key, val, encrypt=False, cnx=None):
        Setting._set(key, val, encrypt, cnx.cursor())

    @staticmethod
    def _get(key, encrypt, cursor):
        cursor.execute(*Item(key=key).select(name="settings", columns=["val"]))
        row = cursor.fetchone()
        return (_encrypt.decode(row[0]) if encrypt else pickle.loads(row[0])) if row else  None


    @staticmethod
    def _set(key, val, encrypt, cursor):
        name = "settings"
        cursor.execute(*Item(key=key).count(name))
        val = _encrypt.encode(val) if encrypt else pickle.dumps(val)
        row = cursor.fetchone()
        if row[0]:
            cursor.execute(*Item(val=val).update(name, i_name="key", i_id=key, version=True))
        else:
            cursor.execute(*Item(key=key, val=val).insert(name))
        cursor.close()


class Log(object):
    @staticmethod
    @db_call
    def log(message, user=None, flag=LogFlag.INFO, cursor=None):
        Log._log(message, user, flag, cursor)

    @staticmethod
    def _log(message, user, flag, cursor):
        item = Item(message=message, flag=flag)
        if user:
            item.user = user
        cursor.execute(*item.insert("logs"))