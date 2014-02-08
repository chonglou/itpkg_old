__author__ = 'zhengjitang@gmail.com'

import pickle

from brahma.models import Item, State
from brahma.env import transaction
from brahma.utils.database import insert, update, delete, select, count, row2item


class RouterDao:
    @staticmethod
    @transaction()
    def get_manager_state(rid, cursor=None):
        return select(Item(id=rid).select(RouterDao._name(), ["manager", "state"]), one=True)(cursor)

    @staticmethod
    def _name():
        return "itpkg_routers"

    @staticmethod
    @transaction()
    def get_network(rid, cursor=None):
        wan, lan = select(Item(id=rid).select(RouterDao._name(), ["wan", "lan"]), one=True)(cursor)
        return pickle.loads(wan), pickle.loads(lan)

    @staticmethod
    @transaction()
    def get_wan_flag(rid, cursor=None):
        wan, flag = select(Item(id=rid).select(RouterDao._name(), ["wan", "flag"]), one=True)(cursor)
        return pickle.loads(wan), flag

    @staticmethod
    @transaction()
    def get_state(rid, cursor=None):
        return select(Item(id=rid).select(RouterDao._name(), ["state"]))(cursor)

    @staticmethod
    @transaction(False)
    def set_lan(rid, lan, cursor=None):
        update(Item(lan=lan).update(RouterDao._name(), rid, version=True))(cursor)

    @staticmethod
    @transaction(False)
    def set_wan(rid, wan, cursor=None):
        update(Item(wan=wan).update(RouterDao._name(), rid, version=True))(cursor)

    @staticmethod
    @transaction(False)
    def init(rid, flag, wan, lan, cursor=None):
        update(
            Item(flag=flag, lan=pickle.dumps(lan), wan=pickle.dumps(wan), state=State.ENABLE).update(RouterDao._name(),
                                                                                                     rid,
                                                                                                     version=True))(
            cursor)

    @staticmethod
    @transaction()
    def list_by_manager(manager, cursor=None):
        columns = ["id", "name", "details", "flags"]
        return [row2item(row, columns) for row in
                select(Item(manager=manager).select(RouterDao._name(), columns=columns))(cursor)]

    @staticmethod
    @transaction
    def get_info(rid, cursor=None):
        columns = ["name", "details"]
        return row2item(select(Item(id=rid).select(RouterDao._name(), columns=columns), one=True)(cursor))

    @staticmethod
    @transaction(False)
    def set_info(rid, name, details, cursor=None):
        update(Item(name=name, details=details).update(RouterDao._name(), rid))(cursor)

    @staticmethod
    @transaction(False)
    def add(manager, name, details, cursor):
        return insert(Item(name=name, manager=manager, details=details).insert(RouterDao._name()))(cursor)


class DeviceDao:
    @staticmethod
    def _name():
        return "itpkg_devices"

    @staticmethod
    def _columns():
        return ["id", "mac", "ip", "fix", "state", "limit", "user"]

    @staticmethod
    @transaction()
    def get_mac(did, cursor=None):
        return select(Item(id=did).select(DeviceDao._name(), ["id"]), one=True)(cursor)

    @staticmethod
    @transaction()
    def list_enable_mac(rid, cursor=None):
        return select(Item(router=rid, state=State.ENABLE).select(DeviceDao._name(), ["id"]))(cursor)

    @staticmethod
    @transaction(False)
    def add_all(rid, mac_ips, cursor=None):
        i, u = 0, 0
        for mac, ip in mac_ips:
            did = select(Item(router=rid, mac=mac).select(DeviceDao._name(), ["id"]))(cursor)
            if did:
                update(Item(ip=ip).update(DeviceDao._name(), id_val=id, version=True))(cursor)
                u += 1
            else:
                insert(Item(router=rid, ip=ip, mac=mac).insert(DeviceDao._name()))(cursor)
                i += 1
        return i, u

    @staticmethod
    @transaction()
    def get_router(did, cursor=None):
        return select(Item(id=did).select(DeviceDao._name(), ["router"]), one=True)(cursor)

    @staticmethod
    @transaction()
    def get(did, cursor=None):
        return row2item(select(Item(id=did).select(DeviceDao._name(), DeviceDao._columns()), one=True)(cursor),
                        DeviceDao._columns())

    @staticmethod
    @transaction()
    def choices(rid, state, cursor=None):
        return select(Item(router=rid, state=state).select(DeviceDao._name(), ["id", "state"]))(cursor)

    @staticmethod
    @transaction()
    def list_by_router(rid, cursor=None):
        return [row2item(r, DeviceDao._columns()) for r in
                select(Item(router=rid).select(DeviceDao._name(), columns=DeviceDao._columns()))(cursor)]

    @staticmethod
    @transaction()
    def list_all_fix(rid, cursor=None):
        return [row2item(r, DeviceDao._columns()) for r in
                select(Item(router=rid, fix=1).select(DeviceDao._name(), columns=DeviceDao._columns()))(cursor)]

    @staticmethod
    @transaction()
    def mac_ip_all_fix(rid, cursor=None):
        return select(Item(router=rid, fix=1).select(DeviceDao._name(), columns=["mac", "ip"]))(cursor)

    @staticmethod
    @transaction(False)
    def set(did, user, limit, state, details, cursor=None):
        update(Item(user=user, limit=limit, state=state, details=details).update(DeviceDao._name(), id_val=did))(cursor)

    @staticmethod
    @transaction()
    def is_ip_inuse(router, ip, cursor=None):
        return count(Item(router=router, ip=ip).count(DeviceDao._name()))(cursor) != 0

    @staticmethod
    @transaction()
    def set_fix(did, ip, fix, cursor=None):
        update(Item(ip=ip, fix=1 if fix else 0).update(DeviceDao._name(), did, version=True))(cursor)


class UserDao:
    @staticmethod
    def _name(): return "itpkg_users"

    @staticmethod
    @transaction()
    def get_name(uid, cursor=None):
        return select(Item(id=uid).select(UserDao._name(), columns=["name"]), one=True)(cursor)

    @staticmethod
    @transaction()
    def choices_by_manager(manager, cursor=None):
        return select(Item(manager=manager).select(UserDao._name(), columns=["id", "name"]))(cursor)

    @staticmethod
    @transaction()
    def get_manager(uid, cursor=None):
        return select((Item(id=uid).select(UserDao._name(), columns=["manager"])), one=True)(cursor)

    @staticmethod
    def _columns():
        return ["id", "name", "details"]

    @staticmethod
    @transaction()
    def list_by_manager(manager, cursor=None):
        return [row2item(row, UserDao._columns()) for row in
                select(Item(manager=manager).select(UserDao._name(), UserDao._columns()))(cursor)]

    @staticmethod
    @transaction()
    def get(uid, cursor=None):
        return row2item(select(Item(id=uid).select(UserDao._name(), UserDao._columns()), one=True)(cursor),
                        UserDao._columns())

    @staticmethod
    @transaction(False)
    def add(manager, name, details, cursor=None):
        return insert(Item(manager=manager, name=name, details=details).insert(UserDao._name()))(cursor)

    @staticmethod
    @transaction(False)
    def set(uid, name, details, cursor=None):
        return update(Item(name=name, details=details).update(UserDao._name(), uid))(cursor)


class LimitDao:
    @staticmethod
    def _name(): return "itpkg_limits"

    @staticmethod
    @transaction()
    def choices_by_manager(manager, cursor=None):
        return select(Item(manager=manager).select(LimitDao._name(), columns=["id", "name"]))(cursor)

    @staticmethod
    @transaction()
    def get_name(lid, cursor=None):
        return select(Item(id=lid).select(LimitDao._name(), columns=["name"]), one=True)(cursor)

    @staticmethod
    @transaction()
    def check_state(lid, cursor=None):
        return select(Item(id=lid).select(LimitDao._name(), columns=["manager", "state"]), one=True)(cursor)

    @staticmethod
    @transaction()
    def get_manager(uid, cursor):
        return select((Item(id=uid).select(LimitDao._name(), columns=["manager"])), one=True)(cursor)

    @staticmethod
    def _columns():
        return ["id", "manager", "name", "up_max", "down_max", "up_min", "down_min"]

    @staticmethod
    @transaction()
    def list_by_manager(manager, cursor=None):
        return [row2item(row, LimitDao._columns()) for row in
                select(Item(manager=manager).select(LimitDao._name(), LimitDao._columns()))(cursor)]

    @staticmethod
    @transaction(False)
    def add(manager, name, up_max, down_max, up_min, down_min, cursor=None):
        insert(
            Item(manager=manager, name=name, up_max=up_max, down_max=down_max, up_min=up_min, down_min=down_min).insert(
                LimitDao._name()))(cursor)

    @staticmethod
    @transaction(False)
    def set(lid, name, up_max, down_max, up_min, down_min, cursor=None):
        update(
            Item(name=name, up_max=up_max, down_max=down_max, up_min=up_min, down_min=down_min).update(LimitDao._name(),
                                                                                                       lid,
                                                                                                       version=True))(
            cursor)

    @staticmethod
    @transaction()
    def get(lid, cursor):
        return row2item(select(Item(id=lid).select(LimitDao._name(), LimitDao._columns()), one=True)(cursor),
                        LimitDao._columns())


class InputDao:
    @staticmethod
    def _name(): return "itpkg_inputs"

    @staticmethod
    def _columns(): return ["id", "name", "port", "protocol"]

    @staticmethod
    @transaction()
    def is_exist(router, port, protocol, cursor=None):
        return count(Item(router=router, port=port, protocol=protocol).count(InputDao._name()))(cursor) > 0

    @staticmethod
    @transaction(False)
    def add(router, port, protocol, cursor=None):
        return insert(Item(router=router, port=port, protocol=protocol).insert(InputDao._name()))(cursor) > 0

    @staticmethod
    @transaction(False)
    def set(iid, port, protocol, cursor=None):
        return update(Item(port=port, protocol=protocol).update(InputDao._name(), iid, version=True))(cursor) > 0


    @staticmethod
    @transaction()
    def get(nid, cursor=None):
        return row2item(select(Item(id=nid).select(InputDao._name(), InputDao._columns()), one=True)(cursor),
                        InputDao._columns())

    @staticmethod
    @transaction()
    def all(rid, cursor=None):
        return [row2item(row, InputDao._columns()) for row in select(
            Item(router=rid).select(InputDao._name(), InputDao._columns())
        )(cursor)]

    @staticmethod
    @transaction(False)
    def delete(iid, cursor=None):
        delete(Item(id=iid).delete(InputDao._name()))(cursor)

    @staticmethod
    @transaction()
    def get_router(iid, cursor=None):
        return select(Item(id=iid).select(InputDao._name(), ["router"]))(cursor)


class OutputDao:
    @staticmethod
    def _name(): return "itpkg_outputs"

    @staticmethod
    def _columns(): return ["id", "name", "flag", "keyword", "begin", "end", "weekdays"]

    @staticmethod
    @transaction()
    def get(nid, cursor=None):
        return row2item(select(Item(id=nid).select(OutputDao._name(), OutputDao._columns()), one=True)(cursor),
                        OutputDao._columns())

    @staticmethod
    @transaction()
    def all(rid, cursor=None):
        return [row2item(row, OutputDao._columns()) for row in select(
            Item(router=rid).select(OutputDao._name(), OutputDao._columns())
        )(cursor)]

    @staticmethod
    @transaction(False)
    def delete(oid, cursor=None):
        delete(Item(id=oid).delete(OutputDao._name()))(cursor)

    @staticmethod
    @transaction()
    def get_router(oid, cursor=None):
        return select(Item(id=oid).select(OutputDao._name(), ["router"]))(cursor)

    @staticmethod
    @transaction(False)
    def set(oid, name, keyword, begin, end, weekdays, cursor):
        update(Item(name=name, keyword=keyword, begin=begin, end=end, weekdays=weekdays).update(OutputDao._name(), oid,
                                                                                                version=True))(cursor)

    @staticmethod
    @transaction(False)
    def add(router, name, keyword, begin, end, weekdays, cursor):
        insert(Item(router=router, name=name, keyword=keyword, begin=begin, end=end, weekdays=weekdays).insert(
            OutputDao._name()))(cursor)


class NatDao:
    @staticmethod
    def _name(): return "itpkg_nats"

    @staticmethod
    def _columns(): return ["id", "name", "sport", "protocol", "dip", "dport"]

    @staticmethod
    @transaction()
    def get(nid, cursor=None):
        return row2item(select(Item(id=nid).select(NatDao._name(), NatDao._columns()), one=True)(cursor),
                        NatDao._columns())

    @staticmethod
    @transaction()
    def all(rid, cursor=None):
        return [row2item(row, NatDao._columns()) for row in select(
            Item(router=rid).select(NatDao._name(), NatDao._columns())
        )(cursor)]

    @staticmethod
    @transaction()
    def is_exist(router, sport, protocol, cursor=None):
        return count(Item(router=router, sport=sport, protocol=protocol).count(NatDao._name()))(cursor) > 0

    @staticmethod
    @transaction(False)
    def set(nid, sport, protocol, dip, dport, cursor=None):
        update(Item(sport=sport, protocol=protocol, dip=dip, dport=dport).update(NatDao._name(), nid, version=True))(
            cursor)

    @staticmethod
    @transaction(False)
    def add(router, sport, protocol, dip, dport, cursor=None):
        insert(Item(router=router, sport=sport, protocol=protocol, dip=dip, dport=dport).insert(NatDao._name()))(cursor)

    @staticmethod
    @transaction(False)
    def delete(nid, cursor=None):
        delete(Item(id=nid).delete(NatDao._name()))(cursor)

    @staticmethod
    @transaction()
    def get_router(nid, cursor=None):
        return select(Item(id=nid).select(NatDao._name(), ["router"]))(cursor)


class OutputDeviceDao:
    @staticmethod
    def _name(): return "itpkg_output_device"

    @staticmethod
    @transaction()
    def list_device(oid, cursor=None):
        return select(Item(output=oid).select(OutputDeviceDao._name(), ["device"]))(cursor)


class GroupDao:
    @staticmethod
    def _name(): return "itpkg_groups"

    @staticmethod
    def _columns(): return ["id", "name", "details"]

    @staticmethod
    @transaction()
    def get(gid, cursor=None):
        return row2item(select(Item(id=gid).select(GroupDao._name(), GroupDao._columns()), one=True)(cursor),
                        GroupDao._columns())

    @staticmethod
    @transaction()
    def all(manager, cursor=None):
        return [row2item(row, GroupDao._columns()) for row in
                select(Item(manager=manager).select(GroupDao._name(), GroupDao._columns()))(cursor)]

    @staticmethod
    @transaction(False)
    def add(manager, name, details, cursor=None):
        insert(Item(manager=manager, name=name, details=details).insert(GroupDao._name()))(cursor)

    @staticmethod
    @transaction(False)
    def set(gid, name, details, cursor=None):
        update(Item(name=name, details=details).update(GroupDao._name(), gid))(cursor)

    @staticmethod
    @transaction()
    def get_manager(gid, cursor):
        return select((Item(id=gid).select(GroupDao._name(), columns=["manager"])), one=True)(cursor)
