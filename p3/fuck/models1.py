__author__ = 'zhengjitang@gmail.com'

import datetime

from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, DateTime, Sequence, LargeBinary, Text


Base = declarative_base()


class Task(Base):
    __tablename__ = "tasks"
    id = Column(Integer, Sequence('task_id_seq'), primary_key=True)
    flag = Column(String(8))
    request = Column(LargeBinary)
    nextRun = Column(DateTime, nullable=False)
    created = Column(DateTime, nullable=False)
    index = Column(Integer, name="index_", nullable=False, default=0)
    total = Column(Integer, nullable=False)
    space = Column(Integer, nullable=False)
    version = Column(Integer, nullable=False, default=0)
    begin = Column(DateTime, name="begin_", nullable=False)
    end = Column(DateTime, name="end_", nullable=False)

    def __init__(self, flag, request, begin, end, total, space, nextRun):
        self.flag = flag
        self.request = request
        self.begin = begin
        self.end = end
        self.total = total
        self.space = space
        self.nextRun = nextRun
        self.created = datetime.datetime.now()


class Permission(Base):
    __tablename__ = "rbacs"
    id = Column(Integer, Sequence('rbac_id_seq'), primary_key=True)
    resource = Column(String(255), name="resource", nullable=False)
    role = Column(String(255), nullable=False)
    operation = Column(String(255), nullable=False)
    created = Column(DateTime, nullable=False)
    begin = Column(DateTime, nullable=False)
    end = Column(DateTime, nullable=False)

    def __init__(self, role, operation, resource, begin, end):
        self.resource = resource
        self.role = role
        self.operation = operation
        self.begin = begin
        self.end = end
        self.created = datetime.datetime.now()


class FriendLink(Base):
    __tablename__ = "friend_links"
    id = Column(Integer, Sequence('friendlink_id_seq'), primary_key=True)
    url = Column(String(255), nullable=False)
    logo = Column(String(255))
    name = Column(String(255), nullable=False)

    def __init__(self, name, url, logo):
        self.url = url
        self.logo = logo
        self.name = name


class Setting(Base):
    __tablename__ = "settings"
    key = Column(String(255), Sequence('setting_id_seq'), name="kkk", primary_key=True)
    val = Column(LargeBinary, name="vvv")
    created = Column(DateTime, nullable=False)
    version = Column(Integer, nullable=False, default=0)

    def __init__(self, key, val):
        self.key = key
        self.val = val
        self.created = datetime.datetime.now()

    def __repr__(self):
        return "<Setting(%s, %s)>" % (self.key, self.created)


class Log(Base):
    __tablename__ = "logs"
    id = Column(Integer, Sequence('log_id_seq'), primary_key=True)
    user = Column(Integer)
    message = Column(String(255), nullable=False)
    flag = Column(String(8), nullable=False)
    created = Column(DateTime, nullable=False)




class User(Base):
    __tablename__ = "users"
    id = Column(Integer, Sequence('user_id_seq'), primary_key=True)
    openid = Column(String(255), unique=True, nullable=False)
    token = Column(String(255))
    email = Column(String(255), unique=True, nullable=False)
    flag = Column(String(8), nullable=False)
    password = Column(String(512), nullable=False)
    username = Column(String(255))
    salt = Column(String(8), nullable=False)
    state = Column(String(8), nullable=False)
    logo = Column(String(255))
    contact = Column(Text)
    created = Column(DateTime, nullable=False)
    lastLogin = Column(DateTime)
