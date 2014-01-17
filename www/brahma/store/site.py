__author__ = 'zhengjitang@gmail.com'

import pickle
from sqlalchemy.orm.exc import NoResultFound
from brahma.models import Setting
from brahma.env import encrypt as _encrypt, db_call


class SiteDao:
    @staticmethod
    @db_call
    def set(key, val, encrypt=False, session=None):
        try:
            s = session.query(Setting).filter(Setting.key == key).one()
        except NoResultFound:
            return

        if encrypt:
            val = _encrypt.encode(val)
        else:
            val = pickle.dumps(val)

        if s:
            s.val = val
            s.val += 1
        else:
            s = Setting(key, val)
            session.add(s)
        pass

    @staticmethod
    @db_call
    def get(key, encrypt=False, session=None):
        try:
            s = session.query(Setting).filter(Setting.key == key).one()
        except NoResultFound:
            return None

        if encrypt:
            return _encrypt.decode(s.val)

        return pickle.loads(s.val)


