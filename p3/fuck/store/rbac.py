__author__ = 'zhengjitang@gmail.com'

import datetime
import logging

from sqlalchemy.orm.exc import NoResultFound
from brahma.models import Permission
from brahma.env import db_call


class RbacDao:
    @staticmethod
    @db_call
    def bind(role, operation, resource, begin=None, end=datetime.datetime.max, bind=False,
             session=None):
        if not begin:
            begin = datetime.datetime.now()

        try:
            p = session.query(Permission).filter(Permission.role == role, Permission.operation == operation,
                                                 Permission.resource == resource).one()
        except NoResultFound:
            p = None
        if bind:
            if p:
                p.begin = begin
                p.end = end
            else:
                p = Permission(role=role, operation=operation, resource=resource, begin=begin, end=end)
                session.add(p)
        else:
            if p:
                session.delete(p)
            else:
                logging.error("用户[%s, %s, %s]权限不存在" % (role, operation, resource))

    @staticmethod
    @db_call
    def auth(role, operation, resource, session=None):
        try:
            dt = datetime.datetime.now()
            return session.query(Permission).filter(
                Permission.role == role,
                Permission.operation == operation,
                Permission.resource == resource,
                Permission.begin < dt,
                Permission.end > dt).one() is not None
        except NoResultFound:
            return False

    @staticmethod
    def bind2admin(user, begin=None, end=datetime.datetime.max, bind=False):
        RbacDao.bind(role="user://%d" % user, operation="MANAGER", resource="SITE", begin=begin, end=end, bind=bind)


    @staticmethod
    def auth4admin(user):
        return RbacDao.auth("user://%d" % user, "MANAGER", "SITE")
