__author__ = 'zhengjitang@gmail.com'

import pickle

from sqlalchemy.orm.exc import NoResultFound

from brahma.models import Setting, User, Log
from brahma.env import encrypt as _encrypt, db_call


class LogDao:
    @staticmethod
    @db_call
    def add_log(message, flag="INFO", user=None, session=None):
        session.add(Log(message=message, flag=flag, user=user))


class UserDao:
    @staticmethod
    @db_call
    def set_password(uid, password, session=None):
        u = session.query(User).filter(User.id == uid).one()
        from brahma.env import encrypt

        salt = encrypt.random_str(8)
        u.salt = salt
        u.password = encrypt.sha512(password + salt)

    @staticmethod
    @db_call
    def set_state(uid, state, session=None):
        u = session.query(User).filter(User.id == uid).one()
        u.state = state

    @staticmethod
    @db_call
    def add_user(flag, email=None, password=None, username=None, openid=None, session=None):
        u = User(flag=flag, email=email, password=password, username=username, openid=openid)
        u.state = "SUBMIT"
        session.add(u)

    @staticmethod
    @db_call
    def get_by_id(uid, session=None):
        try:
            return session.query(User).filter(User.id == uid).one()
        except NoResultFound:
            pass

    @staticmethod
    @db_call
    def get_by_email(email, session=None):
        try:
            return session.query(User).filter(User.email == email).one()
        except NoResultFound:
            pass

    @staticmethod
    @db_call
    def auth(flag, email=None, password=None, session=None):
        if flag == "email":
            try:
                user = session.query(User).filter(User.email == email, User.flag == flag).one()
                if user.check(password):
                    return user
            except NoResultFound:
                pass


class SettingDao:
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


