__author__ = 'zhengjitang@gmail.com'

import datetime
from brahma.utils.enum import Enum


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
        "key_ VARCHAR(16) UNIQUE NOT NULL",
        "val_ BLOB NOT NULL",
    ]),
    ("tasks", True, True, True, [
        "flag_ CHAR(1) NOT NULL",
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
        "flag_ CHAR(1) NOT NULL DEFAULT '%s'" % LogFlag.INFO,
    ]),
    ("permissions", True, True, True, [
        "resource_ VARCHAR(16) NOT NULL",
        "role_ VARCHAR(8) NOT NULL",
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
        "password_ CHAR(128)",
        "openid_ VARCHAR(255)",
        "token_ VARCHAR(255)",
        "flag_ CHAR(1) NOT NULL",
        "salt_ CHAR(8) NOT NULL",
        "state_ CHAR(1) NOT NULL DEFAULT '%s'" % State.SUBMIT,
        "details_ BLOB NOT NULL",
        "last_login_ DATETIME",
    ])
]
