__author__ = 'zhengjitang@gmail.com'

from brahma.env import db_call
from brahma.plugins.itpkg.models import Router, User, Group


class UserDao:
    @staticmethod
    @db_call
    def add(manager, name, details, session=None):
        session.add(User(manager, name, details))

    @staticmethod
    @db_call
    def get(uid, session=None):
        return session.query(User).filter(User.id == uid).one()

    @staticmethod
    @db_call
    def set_info(uid, name, details, session=None):
        u = session.query(User).filter(User.id == uid).one()
        u.name = name
        u.details = details

    @staticmethod
    @db_call
    def all(manager, session=None):
        return session.query(User).filter(User.manager == manager).order_by(User.id.desc()).all()


class GroupDao:
    @staticmethod
    @db_call
    def add(manager, name, details, session=None):
        session.add(Group(manager, name, details))

    @staticmethod
    @db_call
    def get(gid, session=None):
        return session.query(Group).filter(Group.id == gid).one()

    @staticmethod
    @db_call
    def set_info(gid, name, details, session=None):
        g = session.query(Group).filter(Group.id == gid).one()
        g.name = name
        g.details = details
        g.version += 1

    @staticmethod
    @db_call
    def all(manager, session=None):
        return session.query(Group).filter(Group.manager == manager).order_by(Group.id.desc()).all()


class RouterDao:
    @staticmethod
    @db_call
    def set_wan(rid, flag, mac, dns1, dns2, ip=None, netmask=None, gateway=None, username=None, password=None, session=None):
        r  = session.query(Router).filter(Router.id == rid).one()
        import json
        r.wan = json.dumps({"flag":flag, "mac":mac, "dns1":dns1, "dns2":dns2, "ip":ip, "netmask":netmask, "gateway":gateway, "username":username, "password":password})
    @staticmethod
    @db_call
    def set_lan(rid, mac, net, domain, session=None):
        r = session.query(Router).filter(Router.id == rid).one()
        import json
        r.lan = json.dumps({"mac":mac, "net":net, "domain":domain})

    @staticmethod
    @db_call
    def set_state(rid, state, session=None):
        r  = session.query(Router).filter(Router.id == rid).one()
        r.version +=1
        r.state = state
    @staticmethod
    @db_call
    def add(manager, name, details, session=None):
        session.add(Router(manager, name, details))

    @staticmethod
    @db_call
    def set_info(rid, name, details, session=None):
        r = session.query(Router).filter(Router.id == rid).one()
        r.name = name
        r.details = details
        r.version += 1

    @staticmethod
    @db_call
    def get(rid, session=None):
        return session.query(Router).filter(Router.id == rid).one()


    @staticmethod
    @db_call
    def all(manager, session=None):
        return session.query(Router).filter(Router.manager == manager).order_by(Router.id.desc()).all()