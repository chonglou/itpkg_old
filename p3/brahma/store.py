__author__ = 'zhengjitang@gmail.com'

import pickle
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
    def get(key, encrypt=False, cnx=None):
        cursor = cnx.cursor()
        cursor.execute("SELECT val_ FROM settings WHERE key_=%s", [key])
        row = cursor.fetchone()
        return (_encrypt.decode(row[0]) if encrypt else pickle.loads(row[0])) if row else  None


    @staticmethod
    @db_call
    def set(key, val, encrypt=False, cnx=None):
        cursor = cnx.cursor()
        cursor.execute("SELECT COUNT(*) FROM settings WHERE key_=%s", [key])
        val = _encrypt.encode(val) if encrypt else pickle.dumps(val)

        row = cursor.fetchone()
        import logging

        logging.debug("########## %s %s" % (type(row[0]), row[0]))

        if row[0]:
            cursor.execute("UPDATE settings SET version_=version_+1,val_=%s WHERE key_=%s", [val, key])
        else:
            cursor.execute("INSERT INTO settings(key_,val_) VALUES(%s, %s)", [key, val])


class Log(object):
    created = None

    def __init__(self, message, user=None, flag='I'):
        self.message = message
        self.user = user
        self.flag = flag

    def __repr__(self):
        return "<Log(%s, %s)>" % (self.created, self.message)