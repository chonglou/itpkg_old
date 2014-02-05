__author__ = 'zhengjitang@gmail.com'

import datetime

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
    def get(key, encrypt=False):
        return key

    @staticmethod
    def set(key, val, encrypt=False):
        pass


class Log(object):
    created = None

    def __init__(self, message, user=None, flag='I'):
        self.message = message
        self.user = user
        self.flag = flag

    def __repr__(self):
        return "<Log(%s, %s)>" % (self.created, self.message)


tables = [
    ("settings", False, True, True, [
        "key_ VARCHAR(16) UNIQUE NOT NULL",
        "val_ BLOB NOT NULL",
    ]),
    ("tasks", True, True, True, [
        "flag_ CHAR(1) NOT NULL DEFAULT 'S'",
        "request_ BLOB",
        "index_ INTEGER NOT NULL DEFAULT 0",
        "total_ INTEGER NOT NULL DEFAULT 0",
        "space_ INTEGER NOT NULL DEFAULT 0",
        "next_run DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.max,
        "begin_ DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.min,
        "end_ DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.max,
    ]),
    ("logs", True, True, False, [
        "user_ INTEGER NOT NULL DEFAULT 0",
        "message_ VARCHAR(255) NOT NULL",
        "flag_ CHAR(1) NOT NULL DEFAULT 'I'",
    ]),
    ("permissions", True, True, True, [
        "resource_ VARCHAR(16) NOT NULL",
        "role_ VARCHAR(8) NOT NULL",
        "operation_ CHAR(1) NOT NULL DEFAULT 'A'",
        "begin_ DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.min,
        "end_ DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.max,
    ]),
    ("friend_links", True, True, False, [
        "logo_ VARCHAR(255)",
        "name_ VARCHAR(255) NOT NULL",
        "domain_ VARCHAR(32) NOT NULL",
    ]),
    ("users", True, True, True, [
        "email_ VARCHAR(32)",
        "username_ VARCHAR(32) NOT NULL DEFAULT '用户'",
        "password_ CHAR(128)",
        "openid_ VARCHAR(255)",
        "token_ VARCHAR(255)",
        "flag_ CHAR(1) NOT NULL DEFAULT 'M'",
        "salt_ CHAR(8) NOT NULL",
        "state_ CHAR(1) NOT NULL DEFAULT 'S'",
        "details_ BLOB NOT NULL",
        "last_login_ DATETIME",
    ])
]
