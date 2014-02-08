__author__ = 'zhengjitang@gmail.com'

import datetime


class Item(object):
    def insert(self, name):
        ks, l, vs = self.__sql()
        return "INSERT INTO %s(%s) VALUES (%s)" % (
            name,
            ", ".join(ks),
            ", ".join(["%s" for i in range(0, l)])
        ), vs

    def delete(self, name, flag=False):
        ks, l, vs = self.__sql()
        return "DELETE FROM %s WHERE %s" % (
            name,
            (" or " if flag else " and ").join(["%s=%%s" % k for k in ks])
        ), vs

    def update(self, name, id_val, id_name="id", version=False):
        ks, l, vs = self.__sql()
        vs.append(id_val)
        return "UPDATE %s SET %s%s WHERE %s=%%s" % (
            name,
            ", ".join("%s=%%s" % i for i in ks),
            ", version_=version_+1" if version else "",
            "%s_" % id_name
        ), vs

    def select(self, name, columns, flag=False):
        ks, l, vs = self.__sql()
        return "SELECT %s FROM %s %s" % (
            ", ".join(["%s_" % i for i in columns]),
            name,
            ("WHERE %s" % (" or " if flag else " and ").join(["%s=%%s" % k for k in ks])) if len(self) else ""
        ), vs

    def count(self, name, flag=False):
        ks, l, vs = self.__sql()
        return "SELECT COUNT(*) FROM %s WHERE %s" % (
            name,
            (" or " if flag else " and ").join(["%s=%%s" % k for k in ks])
        ), vs

    def __sql(self):
        ks = list()
        vs = list()
        for n in self:
            ks.append(n + "_")
            vs.append(getattr(self, n))
        return ks, len(self), vs

    def set(self, name, value):
        self.__setattr__(name, value)

    def __len__(self):
        return len(self.__dict__)

    def __iter__(self):
        for k in self.__dict__:
            if not k.startswith('__'):
                yield k

    def __init__(self, *args, **kwargs):
        for k in kwargs:
            self.set(k, kwargs[k])

    def __getattr__(self, item):
        return self.__dict__[item] if item in self.__dict__ else None

    def __str__(self):
        return "%s" % self.__dict__


class Enum(object):
    pass


class TaskFlag(Enum):
    RSS = 'rss'
    ROBOTS = 'robots'
    QR = 'qr'
    SITEMAP = 'sitemap'
    ECHO = 'echo'
    EMAIL = 'email'


class State(Enum):
    ENABLE = 'Y'
    DISABLE = 'N'
    SUBMIT = 'S'
    DONE = 'D'


class UserFlag(Enum):
    GOOGLE = 'G'
    EMAIL = 'M'
    QQ = 'Q'


class LogFlag(Enum):
    INFO = 'I'
    DEBUG = 'D'
    WARN = 'W'
    ERROR = 'E'


class Operation(Enum):
    EDIT = 'E'
    MANAGER = 'M'
    VIEW = 'V'
    NONE = 'N'


tables = [
    ("settings", False, True, True, [
        "key_ VARCHAR(32) UNIQUE NOT NULL",
        "val_ BLOB NOT NULL",
    ]),
    ("tasks", True, True, True, [
        "flag_ CHAR(8) NOT NULL",
        "request_ BLOB",
        "index_ INTEGER NOT NULL DEFAULT 0",
        "total_ INTEGER NOT NULL DEFAULT 0",
        "space_ INTEGER NOT NULL DEFAULT 0",
        "next_run_ DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.max,
        "begin_ DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.min,
        "end_ DATETIME NOT NULL DEFAULT '%s'" % datetime.datetime.max,
    ]),
    ("logs", True, True, False, [
        "user_ INTEGER NOT NULL DEFAULT 0",
        "message_ VARCHAR(255) NOT NULL",
        "flag_ CHAR(1) NOT NULL DEFAULT '%s'" % LogFlag.INFO,
    ]),
    ("permissions", True, True, True, [
        "resource_ VARCHAR(32) NOT NULL",
        "role_ VARCHAR(32) NOT NULL",
        "operation_ CHAR(1) NOT NULL DEFAULT '%s'" % Operation.NONE,
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
        "password_ CHAR(96)",
        "openid_ VARCHAR(255)",
        "token_ VARCHAR(255)",
        "flag_ CHAR(1) NOT NULL",
        "salt_ CHAR(8) NOT NULL",
        "state_ CHAR(1) NOT NULL DEFAULT '%s'" % State.SUBMIT,
        "logo_ VARCHAR(128)",
        "contact_ BLOB",
        "last_login_ DATETIME",
    ]),
    ("histories", True, True, True, [
        "url_ VARCHAR(32) NOT NULL",
        "content_ BLOB NOT NULL",
    ]),
]
