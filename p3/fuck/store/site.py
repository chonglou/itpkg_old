__author__ = 'zhengjitang@gmail.com'

import pickle

from sqlalchemy.orm.exc import NoResultFound

from brahma.models1 import Setting, User, Log, FriendLink
from brahma.env import encrypt as _encrypt


class FriendLinkDao:
    @staticmethod
    def add(name, url, logo=None, session=None):
        session.add(FriendLink(name, url, logo))

    @staticmethod
    def all(session=None):
        return session.query(FriendLink).order_by(FriendLink.id.desc()).all()

    @staticmethod
    
    def get(flid, session=None):
        return session.query(FriendLink).filter(FriendLink.id == flid).one()

    @staticmethod
    
    def set(flid, name, url, logo=None, session=None):
        fl = session.query(FriendLink).filter(FriendLink.id == flid).one()
        fl.name = name
        fl.url = url
        fl.logo = logo

    @staticmethod
    
    def delete(flid, session=None):
        session.query(FriendLink).filter(FriendLink.id == flid).delete()


class LogDao:
    @staticmethod
    
    def list_log(begin, end, user, limit=None, session=None):
        q = session.query(Log).filter(Log.user == user, Log.created >= begin, Log.created <= end).order_by(
            Log.id.desc())
        return q.limit(limit) if limit else q.all()

    @staticmethod
    
    def add_log(message, flag="INFO", user=None, session=None):
        session.add(Log(message=message, flag=flag, user=user))


class UserDao:
    @staticmethod
    
    def list_user(session=None):
        return session.query(User).order_by(User.id.desc()).all()

    @staticmethod
    
    def set_info(uid, username, logo, contact, session=None):
        u = session.query(User).filter(User.id == uid).one()
        import json

        u.username = username
        u.logo = logo
        u.contact = json.dumps(contact)


    @staticmethod
    
    def set_lastLogin(uid, session=None):
        import datetime

        u = session.query(User).filter(User.id == uid).one()
        u.lastLogin = datetime.datetime.now()

    @staticmethod
    
    def set_password(uid, password, session=None):
        u = session.query(User).filter(User.id == uid).one()
        from brahma.env import encrypt

        salt = encrypt.random_str(8)
        u.salt = salt
        u.password = encrypt.sha512(password + salt)

    @staticmethod
    
    def set_state(uid, state, session=None):
        u = session.query(User).filter(User.id == uid).one()
        u.state = state

    @staticmethod
    
    def add_user(flag, email=None, password=None, username=None, openid=None, session=None):
        u = User(flag=flag, email=email, password=password, username=username, openid=openid)
        u.state = "SUBMIT"
        session.add(u)

    @staticmethod
    
    def get_by_id(uid, session=None):
        try:
            return session.query(User).filter(User.id == uid).one()
        except NoResultFound:
            pass

    @staticmethod
    
    def get_by_email(email, session=None):
        try:
            return session.query(User).filter(User.email == email).one()
        except NoResultFound:
            pass

    @staticmethod
    
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
    
    def set(key, val, encrypt=False, session=None):
        try:
            s = session.query(Setting).filter(Setting.key == key).one()
        except NoResultFound:
            s = None

        if encrypt:
            val = _encrypt.encode(val)
        else:
            val = pickle.dumps(val)

        if s:
            s.val = val
            s.version += 1
        else:
            s = Setting(key, val)
            session.add(s)
        pass

    @staticmethod
    
    def get(key, encrypt=False, session=None):
        try:
            s = session.query(Setting).filter(Setting.key == key).one()
        except NoResultFound:
            return None

        if encrypt:
            return _encrypt.decode(s.val)

        return pickle.loads(s.val)


