__author__ = 'zhengjitang@gmail.com'

import pickle
import datetime
import logging

from brahma.models import Item, LogFlag, Operation, UserFlag, State
from brahma.env import transaction, encrypt as _encrypt
from brahma.utils.database import insert, update, delete, count, select


class User(object):
    @staticmethod
    @transaction(readonly=False)
    def enable(uid, flag, cursor=None):
        User._set_state(uid, State.ENABLE if flag else State.DISABLE, cursor)
        Log.add("启用用户" if flag else "禁用用户", user=uid)

    @staticmethod
    @transaction
    def all(cursor=None):
        return select(Item().select("users", columns=["id", "username", "email", "logo"]))(cursor)

    @staticmethod
    @transaction
    def get_by_id(uid, cursor=None):
        return select(
            Item(id=uid).select("users", columns=["username", "email", "contact"]),
            one=True)(cursor)


    @staticmethod
    @transaction
    def get_id_by_email(email, cursor=None):
        return User._get_id_by_email(email, cursor)

    @staticmethod
    def _auth_email(email, password, cursor):
        row = select(
            Item(flag=UserFlag.EMAIL, email=email).select("users",
                                                          columns=["id", "username", "logo", "password"]),
            one=True)(cursor)

        rs = None
        if row:
            if _encrypt.check(password, row[4]):
                rs = row(0), email, row(1), row(2)
        return rs

    @staticmethod
    def _get_id_by_email(email, cursor):
        return select(Item(email=email, flag=UserFlag.EMAIL).select("users", ["id"]))(cursor)

    @staticmethod
    def _set_state(uid, state, cursor):
        update(Item(state=state).update("users", id_val=uid))(cursor)

    @staticmethod
    def _add_email(username, email, password, cursor):
        return insert(
            Item(username=username, email=email, password=_encrypt.password(password),
                 flag=UserFlag.EMAIL).insert(
                "users"))(cursor)


class Task(object):
    @staticmethod
    def set_next_run(tid, next_run=None):
        pass

    @staticmethod
    def list_available():
        return list()

    @staticmethod
    def get(tid):
        return tid


class Setting(object):
    @staticmethod
    @transaction(readonly=False)
    def startup(flag=True, cursor=None):
        import datetime

        Setting._set(
            "site.startup" if flag else "site.shutdown",
            datetime.datetime.now(),
            False, cursor=cursor)
        Log._add(message="启动系统" if flag else "关闭系统", user=None, flag=LogFlag.INFO, cursor=cursor)

    @staticmethod
    @transaction
    def get(key, encrypt=False, cursor=None):
        return Setting._get(key, encrypt, cursor)

    @staticmethod
    @transaction(readonly=False)
    def set(key, val, encrypt=False, cursor=None):
        Setting._set(key, val, encrypt, cursor)

    @staticmethod
    def _get(key, encrypt, cursor):
        row = select(Item(key=key).select(name="settings", columns=["val"]))(cursor)
        return (_encrypt.decode(row[0]) if encrypt else pickle.loads(row[0])) if row else  None


    @staticmethod
    def _set(key, val, encrypt, cursor):
        name = "settings"
        val = _encrypt.encode(val) if encrypt else pickle.dumps(val)
        c = count(Item(key=key).count(name))(cursor)
        if c:
            update(Item(val=val).update(name, id_name="key", id_val=key, version=True))(cursor)
        else:
            insert(Item(key=key, val=val).insert(name))(cursor)


class FriendLink(object):
    @staticmethod
    @transaction(readonly=False)
    def all(cursor=None):
        return select(Item().select(name="friend_links", columns=["id", "logo", "name", "url"]))(cursor)


class Permission(object):
    @staticmethod
    def _bind(role, operation, resource, begin, end, bind, cursor):
        pid = select(
            Item(role=role, operation=operation, resource=resource).select("permissions", ["id"]),
            one=True)(cursor)
        if bind:
            if pid:
                update(Item(begin=begin, end=end).update("permissions", id_val=pid))(cursor)
            else:
                insert(
                    Item(role=role, operation=operation, resource=resource, begin=begin, end=end).insert(
                        "permissions"
                    ))(cursor)
        else:
            if pid:
                delete(Item(id=pid).delete('permissions'))(cursor)
            else:
                logging.error("权限[%s,%s,%s]不存在" % (role, operation, resource))


    @staticmethod
    def _auth(role, operation, resource, cursor):
        now = datetime.datetime.now()
        row = select(
            Item(role=role, operation=operation, resource=resource).select("permission", ["begin", "end"]),
            one=True)(cursor)
        rs = False
        if row:
            rs = row[0] < now < row[1]
        return rs

    @staticmethod
    @transaction(readonly=True)
    def bind2admin(uid, begin=None, end=datetime.datetime.max, bind=False, cursor=None):

        Permission._bind(role="user://%d" % uid, operation=Operation.MANAGER, resource="SITE",
                         begin=begin, end=end,
                         bind=bind, cursor=cursor)


    @staticmethod
    @transaction
    def auth4admin(uid, cursor=None):
        return Permission._auth(role="user://%d" % uid, operation=Operation.MANAGER, resource="SITE",
                                cursor=cursor)


class Log(object):
    @staticmethod
    @transaction
    def add(message, user=None, flag=LogFlag.INFO, cursor=None):
        Log._add(message, user, flag, cursor)


    @staticmethod
    def _add(message, user, flag, cursor):
        item = Item(message=message, flag=flag)
        if user:
            item.user = user
        insert(item.insert("logs"))(cursor)
