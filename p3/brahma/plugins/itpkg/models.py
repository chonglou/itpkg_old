__author__ = 'zhengjitang@gmail.com'

import datetime

from brahma.models import Enum, State


class RouterFlag(Enum):
    ARCH_LINUX_OLD = "A"
    ARCH_LINUX_NEW = "B"
    OPEN_WRT = "O"


class OutputFlag(Enum):
    KEYWORD = "K"
    DOMAIN = "D"
    ADDRESS = "A"


class Protocol(Enum):
    TCP = "T"
    UDP = "U"


class WanFlag(Enum):
    PPPOE = "P"
    STATIC = "S"
    DHCP = "D"


class Weekday(Enum):
    "mon,tue,wed,thu,fri,sat,sun"
    MONDAY = "mon"
    TUESDAY = "tue"
    WEDNESDAY = "wed"
    THURSDAY = "thu"
    FRIDAY = "fir"
    SATURDAY = "sat"
    SUNDAY = "sun"

    @staticmethod
    def all(days=list()):
        if not days:
            days = [Weekday.MONDAY, Weekday.TUESDAY, Weekday.WEDNESDAY, Weekday.THURSDAY, Weekday.FRIDAY,
                    Weekday.SATURDAY, Weekday.SUNDAY]
        return ",".join(days)


class Wan(object):
    def __init__(self, mac, flag, ip=None, netmask=None, gateway=None, dns1=None, dns2=None, username=None,
                 password=None):
        self.mac = mac
        self.flag = flag
        self.ip = ip
        self.netmask = netmask
        self.gateway = gateway
        self.dns1 = dns1
        self.dns2 = dns2
        self.username = username
        self.password = password


class Lan(object):
    def __init__(self, mac, net, domain):
        self.mac = mac
        self.net = net
        self.domain = domain


tables = [
    ("itpkg_routers", True, True, False, [
        "name_ VARCHAR(32) UNIQUE NOT NULL",
        "wan_ VARBINARY(255) NOT NULL",
        "lan_ VARBINARY(255) NOT NULL",
        "flag_ CHAR(1) NOT NULL",
        "manager_ INTEGER NOT NULL DEFAULT 0",
        "details_ TEXT",
    ]),
    ("itpkg_inputs", True, True, False, [
        "name_ VARCHAR(32) NOT NULL",
        "port_ SMALLINT UNSIGNED NOT NULL",
        "protocol_ CHAR(1) NOT NULL",
        "router_ INTEGER NOT NULL",
    ]),
    ("itpkg_outputs", True, True, False, [
        "name_ VARCHAR(32) NOT NULL",
        "flag_ CHAR(1) NOT NULL",
        "keyword_ VARCHAR(32) NOT NULL",
        "begin_ TIME NOT NULL DEFAULT '%s'" % datetime.time.min,
        "end_ TIME NOT NULL DEFAULT '%s'" % datetime.time.max,
        "weekdays_ CHAR(27) NOT NULL DEFAULT '%s'" % Weekday.all(),
        "router_ INTEGER NOT NULL",
    ]),
    ("itpkg_nats", True, True, False, [
        "name_ VARCHAR(32) NOT NULL",
        "sport_ SMALLINT UNSIGNED NOT NULL",
        "protocol_ CHAR(1) NOT NULL",
        "dip_ TINYINT UNSIGNED NOT NULL",
        "dport_ SMALLINT UNSIGNED NOT NULL",
        "router_ INTEGER NOT NULL",
    ]),
    ("itpkg_output_device", True, True, False, [
        "output_ INTEGER NOT NULL",
        "device_ INTEGER NOT NULL",
    ]),
    ("itpkg_devices", True, True, False, [
        "mac_ CHAR(20) NOT NULL",
        "ip_ TINYINT UNSIGNED NOT NULL",
        "fix_ TINYINT NOT NULL DEFAULT 0",
        "state_ CHAR(1) NOT NULL DEFAULT '%s'" % State.SUBMIT,
        "details_ TEXT",
        "limit_ INTEGER NOT NULL",
        "user_ INTEGER NOT NULL",
        "router_ INTEGER NOT NULL",
    ]),
    ("itpkg_groups", True, True, False, [
        "name_ VARCHAR(32) NOT NULL",
        "state_ CHAR(1) NOT NULL DEFAULT '%s'" % State.SUBMIT,
        "manager_ INTEGER NOT NULL",
        "details_ TEXT",
    ]),
    ("itpkg_users", True, True, False, [
        "name_ VARCHAR(32) NOT NULL",
        "state_ CHAR(1) NOT NULL DEFAULT '%s'" % State.SUBMIT,
        "manager_ INTEGER NOT NULL",
        "details_ TEXT",
    ]),
    ("itpkg_group_user_router", True, True, False, [
        "group_ INTEGER NOT NULL",
        "user_ INTEGER NOT NULL",
        "router_ INTEGER NOT NULL",
    ]),
    ("itpkg_limits", True, True, False, [
        "name_ VARCHAR(32) NOT NULL",
        "manager_ INTEGER NOT NULL",
        "max_up_ SMALLINT UNSIGNED NOT NULL",
        "max_down_ SMALLINT UNSIGNED NOT NULL",
        "min_up_ SMALLINT UNSIGNED NOT NULL",
        "min_down_ SMALLINT UNSIGNED NOT NULL",
        "begin_ TIME NOT NULL DEFAULT '%s'" % datetime.time.min,
        "end_ TIME NOT NULL DEFAULT '%s'" % datetime.time.max,
        "weekdays_ CHAR(27) NOT NULL DEFAULT '%s'" % Weekday.all(),
        "details_ TEXT",
    ]),
]