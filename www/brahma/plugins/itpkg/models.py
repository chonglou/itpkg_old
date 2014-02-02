__author__ = 'zhengjitang@gmail.com'

import datetime

from sqlalchemy import Column, Integer, String, DateTime, Text, Sequence, Boolean, Time

from brahma.models import Base


class Router(Base):
    __tablename__ = "itpkg_routers"
    id = Column(Integer, Sequence('itpkg_router_id_seq'), primary_key=True)
    name = Column(String(255), nullable=False)
    wan = Column(String(512))
    lan = Column(String(512))
    ping = Column(Boolean, nullable=False, default=True)
    manager = Column(Integer, nullable=False)
    details = Column(Text)
    flag = Column(String(16))
    version = Column(Integer, nullable=False, default=0)
    state = Column(String(8), nullable=False, default="SUBMIT")
    created = Column(DateTime, nullable=False)

    def __init__(self, manager, name, details):
        self.manager = manager
        self.name = name
        self.details = details
        self.created =datetime.datetime.now()


class Input(Base):
    __tablename__ = "itpkg_inputs"
    id = Column(Integer, Sequence('itpkg_input_id_seq'), primary_key=True)
    name = Column(String(255), nullable=False)
    port = Column(Integer, nullable=False)
    protocol = Column(String(5), nullable=False)
    router = Column(Integer, nullable=False)

    def __init__(self, router, name, port, protocol):
        self.name = name
        self.router = router
        self.port = port
        self.protocol = protocol


class Output(Base):
    __tablename__ = "itpkg_outputs"
    id = Column(Integer, Sequence('itpkg_output_id_seq'), primary_key=True)
    name = Column(String(255), nullable=False)
    flag = Column(String(8), nullable=False, default="key")
    keyword = Column(String(255), nullable=False)
    begin = Column(String(8), name="begin_", nullable=False, default="08:00")
    end = Column(String(8), name="end_", nullable=False, default="20:00")
    weekdays = Column(String(30), nullable=False, default="mon,tue,wed,thu,fri")
    router = Column(Integer, nullable=False)

    def weekdays_cn(self):
        w = self.weekdays
        for k, v in [('mon', '星期一'), ('tue', '星期二'), ('wed', '星期三'), ('thu', '星期四'), ('fri', '星期五'), ('sat', '星期六'),
                     ('sun', '星期日')]:
            w = w.replace(k, v)
        return "[%s,%s]@[%s]" % (self.begin, self.end, w)

    def __init__(self, router, name, keyword, begin, end, weekdays):
        self.router = router
        self.name = name
        self.begin = begin
        self.end = end
        self.keyword = keyword
        self.weekdays = weekdays


class Nat(Base):
    __tablename__ = "itpkg_nats"
    id = Column(Integer, Sequence('itpkg_nat_id_seq'), primary_key=True)
    name = Column(String(255), nullable=False)
    sport = Column(Integer, nullable=False)
    dport = Column(Integer, nullable=False)
    dip = Column(Integer, nullable=False)
    protocol = Column(String(5), nullable=False)
    router = Column(Integer, nullable=False)

    def __init__(self, router, name, sport, protocol, dip, dport):
        self.router = router
        self.name = name
        self.sport = sport
        self.dip = dip
        self.dport = dport
        self.protocol = protocol


class OutputDevice(Base):
    __tablename__ = "itpkg_output_device"
    id = Column(Integer, Sequence('itpkg_output_device_id_seq'), primary_key=True)
    output = Column(Integer, nullable=False)
    device = Column(Integer, nullable=False)

    def __init__(self, output, device):
        self.output = output
        self.device = device


class RouterDevice(Base):
    __tablename__ = "itpkg_router_device"
    id = Column(Integer, Sequence('itpkg_router_device_id_seq'), primary_key=True)
    router = Column(Integer, nullable=False)
    device = Column(Integer, nullable=False)

    def __init__(self, router, device):
        self.router = router
        self.device = device


class Device(Base):
    __tablename__ = "itpkg_devices"
    id = Column(Integer, Sequence('itpkg_device_id_seq'), primary_key=True)
    mac = Column(String(20))
    ip = Column(Integer, nullable=False)
    fix = Column(Boolean, nullable=False, default=False)
    state = Column(String(8), nullable=False, default="SUBMIT")
    user = Column(Integer)
    limit = Column(Integer, name="limit_")
    router = Column(Integer, nullable=False)
    details = Column(Text)
    version = Column(Integer, nullable=False, default=0)
    lastUpdated = Column(DateTime)
    created = Column(DateTime, nullable=False)

    def __init__(self, router, mac, ip):
        self.router = router
        self.mac = mac
        self.ip = ip
        self.created =datetime.datetime.now()


class Group(Base):
    __tablename__ = "itpkg_groups"
    id = Column(Integer, Sequence('itpkg_group_id_seq'), primary_key=True)
    name = Column(String(255))

    details = Column(Text)
    state = Column(String(8), nullable=False, default="SUBMIT")
    manager = Column(Integer, nullable=False)
    version = Column(Integer, nullable=False, default=0)
    created = Column(DateTime, nullable=False)

    def __init__(self, manager, name, details):
        self.manager = manager
        self.name = name
        self.details = details
        self.created =datetime.datetime.now()


class User(Base):
    __tablename__ = "itpkg_users"
    id = Column(Integer, Sequence('itpkg_user_id_seq'), primary_key=True)
    name = Column(String(255))
    manager = Column(Integer, nullable=False)
    details = Column(Text)
    state = Column(String(8), nullable=False, default="SUBMIT")
    created = Column(DateTime, nullable=False)

    def __init__(self, manager, name, details):
        self.manager = manager
        self.name = name
        self.details = details
        self.created =datetime.datetime.now()


class GroupUser(Base):
    __tablename__ = "itpkg_group_user"
    id = Column(Integer, Sequence('itpkg_group_user_id_seq'), primary_key=True)
    group = Column(Integer, nullable=False)
    user = Column(Integer, nullable=False)
    router = Column(Integer, nullable=False)

    def __init__(self, router, group, user):
        self.router = router
        self.user = user
        self.group = group


class Limit(Base):
    __tablename__ = "itpkg_limits"
    id = Column(Integer, Sequence('itpkg_limit_id_seq'), primary_key=True)
    manager = Column(Integer, nullable=False)
    name = Column(String(255), nullable=False)
    upMax = Column(Integer, nullable=False)
    downMax = Column(Integer, nullable=False)
    upMin = Column(Integer, nullable=False)
    downMin = Column(Integer, nullable=False)
    begin = Column(Time, nullable=False, default=datetime.time())
    end = Column(Time, nullable=False, default=datetime.time(23, 59, 59))
    #weekdays = Column(String(30), nullable=False, default="mon,tue,wed,thu,fri")
    version = Column(Integer, nullable=False, default=0)
    created = Column(DateTime, nullable=False)

    def __init__(self, manager, name, upMax, downMax, upMin, downMin):
        self.manager = manager
        self.name = name
        self.upMax = upMax
        self.upMin = upMin
        self.downMax = downMax
        self.downMin = downMin
        self.created =datetime.datetime.now()





