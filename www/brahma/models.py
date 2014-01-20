__author__ = 'zhengjitang@gmail.com'

import datetime

from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import Column, Integer, String, DateTime, Sequence, LargeBinary, Text


Base = declarative_base()


class Permission(Base):
    __tablename__ = "rbacs"
    id = Column(Integer, Sequence('rbac_id_seq'), primary_key=True)
    resource = Column(String(255), name="resource", nullable=False)
    role = Column(String(255), nullable=False)
    operation = Column(String(255), nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())
    begin = Column(DateTime, nullable=False)
    end = Column(DateTime, nullable=False)

    def __init__(self, role, operation, resource, begin, end):
        self.resource = resource
        self.role = role
        self.operation = operation
        self.begin = begin
        self.end = end


class FriendLink(Base):
    __tablename__ = "friend_links"
    id = Column(Integer, Sequence('rbac_id_seq'), primary_key=True)
    url = Column(String(255), nullable=False, unique=True)
    logo = Column(String(255))
    title = Column(String(255), nullable=False)

    def __init__(self, url, logo, title):
        self.url = url
        self.logo = logo
        self.title = title


class Setting(Base):
    __tablename__ = "settings"
    key = Column(String(255), name="kkk", primary_key=True)
    val = Column(LargeBinary, name="vvv")
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())
    version = Column(Integer, nullable=False, default=0)

    def __init__(self, key, val):
        self.key = key
        self.val = val

    def __repr__(self):
        return "<Setting(%s, %s)>" % (self.key, self.created)


class Log(Base):
    __tablename__ = "logs"
    id = Column(Integer, Sequence('user_id_seq'), primary_key=True)
    user = Column(Integer)
    message = Column(String(255), nullable=False)
    flag = Column(String(8), nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())

    def __init__(self, message, user, flag):
        self.message = message
        self.user = user
        self.flag = flag

    def __repr__(self):
        return "<Log(%s, %s)>" % (self.created, self.message)


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
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())
    lastLogin = Column(DateTime)

    def __init__(self, flag, username=None, password=None, email=None, openid=None, ):
        self.username = username

        from brahma.env import encrypt

        self.salt = encrypt.random_str(8)

        if flag == "email":
            self.username = username
            self.email = email
            self.openid = encrypt.uuid()
            self.flag = flag
        elif flag == "google":
            self.username = encrypt.random_str(8)
            self.email = username + "@" + "localhost"
            self.openid = openid
            self.flag = flag
            password = encrypt.random_str(12)
        elif flag == "qq":
            self.username = encrypt.random_str(8)
            self.email = self.username + "@" + "localhost"
            self.openid = openid
            self.flag = flag
            password = encrypt.random_str(12)

        self.password = encrypt.sha512(password + self.salt)

    def check(self, password):
        from brahma.env import encrypt

        return encrypt.sha512(password + self.salt) == self.password

    def __repr__(self):
        return "<User('%s', '%s')>" % (self.email, self.username)