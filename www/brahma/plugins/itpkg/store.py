__author__ = 'zhengjitang@gmail.com'

import datetime
from sqlalchemy.orm.exc import NoResultFound
from brahma.env import db_call
from brahma.plugins.itpkg.models import Router, User, Group, Device, Input, Output, Nat, OutputDevice, Limit


class LimitDao:
    @staticmethod
    @db_call
    def get(lid, session=None):
        return session.query(Limit).filter(Limit.id == lid).one()

    @staticmethod
    @db_call
    def set(lid, name, upMax, downMax, upMin, downMin, session=None):
        l = session.query(Limit).filter(Limit.id == lid).one()
        l.name = name
        l.upMax = upMax
        l.upMin = upMin
        l.downMax = downMax
        l.downMin = downMin

    @staticmethod
    @db_call
    def add(manager, name, upMax, downMax, upMin, downMin, session=None):
        session.add(Limit(manager, name, upMax, downMax, upMin, downMin))

    @staticmethod
    @db_call
    def all(manager, session=None):
        return session.query(Limit).filter(Limit.manager == manager).all()


class InputDao:
    @staticmethod
    @db_call
    def get(iid, session=None):
        return session.query(Input).filter(Input.id == iid).one()

    @staticmethod
    @db_call
    def delete(iid, session=None):
        return session.query(Input).filter(Input.id == iid).delete()

    @staticmethod
    @db_call
    def is_exist(rid, port, protocol, session=None):
        return session.query(Input).filter(Input.router == rid, Input.port == port,
                                           Input.protocol == protocol).count() != 0

    @staticmethod
    @db_call
    def add(rid, name, port, protocol, session=None):
        session.add(Input(rid, name, port, protocol))

    @staticmethod
    @db_call
    def set(iid, name, port, protocol, session=None):
        i = session.query(Input).filter(Input.id == iid).one()
        i.name = name
        i.port = port
        i.protocol = protocol

    @staticmethod
    @db_call
    def all(rid, session=None):
        return session.query(Input).filter(Input.router == rid).all()


class OutputDao:
    @staticmethod
    @db_call
    def delete(oid, session=None):
        session.query(Output).filter(Output.id == oid).delete()

    @staticmethod
    @db_call
    def get(oid, session=None):
        return session.query(Output).filter(Output.id == oid).one()

    @staticmethod
    @db_call
    def add(rid, name, keyword, begin, end, weekdays, session=None):
        session.add(Output(rid, name, keyword, begin, end, weekdays))

    @staticmethod
    @db_call
    def set(oid, name, keyword, begin, end, weekdays, session=None):
        o = session.query(Output).filter(Output.id == oid).one()
        o.name = name
        o.keyword = keyword
        o.begin = begin
        o.end = end
        o.weekdays = weekdays


    @staticmethod
    @db_call
    def all(rid, session=None):
        return session.query(Output).filter(Output.router == rid).all()


class NatDao:
    @staticmethod
    @db_call
    def set(nid, name, sport, protocol, dip, dport, session=None):
        n = session.query(Nat).filter(Nat.id == nid).one()
        n.name = name
        n.sport = sport
        n.protocol = protocol
        n.dip = dip
        n.dport = dport

    @staticmethod
    @db_call
    def add(rid, name, sport, protocol, dip, dport, session=None):
        session.add(Nat(rid, name, sport, protocol, dip, dport))


    @staticmethod
    @db_call
    def is_exist(rid, sport, protocol, session=None):
        return session.query(Nat).filter(Nat.router == rid, Nat.sport == sport, Nat.protocol == protocol).count() != 0

    @staticmethod
    @db_call
    def get(nid, session=None):
        return session.query(Nat).filter(Nat.id == nid).one()

    @staticmethod
    @db_call
    def delete(nid, session=None):
        session.query(Nat).filter(Nat.id == nid).delete()

    @staticmethod
    @db_call
    def all(rid, session=None):
        return session.query(Nat).filter(Nat.router == rid).all()


class OutputDeviceDao:
    @staticmethod
    @db_call
    def bind(oid, did, bind=False, session=None):
        try:
            od = session.query(OutputDevice).filter(OutputDevice.output == oid, OutputDevice.device == did)
        except NoResultFound:
            od = None

        if od:
            if not bind:
                session.delete(od)
        else:
            if bind:
                session.add(OutputDevice(oid, did))

    @staticmethod
    @db_call
    def all(oid, session=None):
        return session.query(OutputDevice).filter(OutputDevice.output == oid).all()


class DeviceDao:
    @staticmethod
    @db_call
    def all_fix(rid, session=None):
        return session.query(Device).filter(Device.router == rid, Device.fix == True, Device.state == "ENABLE").all()

    @staticmethod
    @db_call
    def bind(did, ip, flag, session=None):
        d = session.query(Device).filter(Device.id == did).one()
        if flag:
            d.ip = ip
            d.fix = True
        else:
            d.fix = False

    @staticmethod
    @db_call
    def set(did, user, limit, state, details, session=None):
        d = session.query(Device).filter(Device.id == did).one()
        d.user = user
        d.details = details
        d.limit = limit
        d.state = state

    @staticmethod
    @db_call
    def is_ip_inuse(rid, ip, session=None):
        return session.query(Device).filter(Device.router == rid, Device.ip == ip, Device.fix == True).count() != 0

    @staticmethod
    @db_call
    def get(did, session=None):
        return session.query(Device).filter(Device.id == did).one()

    @staticmethod
    @db_call
    def fill(rid, items, session=None):
        insert = 0
        update = 0
        for mac, ip in items:
            try:
                d = session.query(Device).filter(Device.mac == mac).filter(Device.router == rid).one()
                if d.ip != ip:
                    d.ip = ip
                    d.lastUpdated = datetime.datetime.now()
                    d.version += 1
                    update += 1
            except NoResultFound:
                session.add(Device(rid, mac, ip))
                insert += 1
        return insert, update

    @staticmethod
    @db_call
    def all_by_state(rid, state, session=None):
        return session.query(Device).filter(Device.router == rid, Device.state == state).order_by(
            Device.state.asc()).all()

    @staticmethod
    @db_call
    def all(rid, session=None):
        return session.query(Device).filter(Device.router == rid).order_by(Device.state.asc()).all()

    @staticmethod
    @db_call
    def delete(did, session=None):
        session.query(Device).filter(Device.id == did).delete()


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
        return session.query(User).filter(User.manager == manager).all()


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
    def set_wan(rid, flag, mac, dns1, dns2, ip=None, netmask=None, gateway=None, username=None, password=None,
                session=None):
        r = session.query(Router).filter(Router.id == rid).one()
        import json

        r.wan = json.dumps(
            {"flag": flag, "mac": mac, "dns1": dns1, "dns2": dns2, "ip": ip, "netmask": netmask, "gateway": gateway,
             "username": username, "password": password})

    @staticmethod
    @db_call
    def set_lan(rid, mac, net, domain, session=None):
        r = session.query(Router).filter(Router.id == rid).one()
        import json

        r.lan = json.dumps({"mac": mac, "net": net, "domain": domain})

    @staticmethod
    @db_call
    def set_state(rid, flag, state, session=None):
        r = session.query(Router).filter(Router.id == rid).one()
        r.flag = flag
        r.state = state
        r.version += 1

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