__author__ = 'zhengjitang@gmail.com'

from brahma.env import db_call
from brahma.plugins.itpkg.models import Router


class RouterDao:
    @staticmethod
    @db_call
    def all(uid, session=None):
        return session.query(Router).filter(Router.manager == uid).all()