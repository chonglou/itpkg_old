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
    def set_info(uid, username, logo, contact, cursor=None):
        update(Item(username=username, logo=logo, contact=pickle.dumps(contact)).update("users", id_val=uid))(cursor)

    @staticmethod
    @transaction(readonly=False)
    def set_password(uid, password, cursor=None):
        update(Item(password=_encrypt.password(password)).update("users", id_val=uid))(cursor)
        Log._add(message="修改密码", user=uid, flag=LogFlag.INFO, cursor=cursor)

    @staticmethod
    @transaction(readonly=False)
    def enable(uid, flag, cursor=None):
        User._set_state(uid, State.ENABLE if flag else State.DISABLE, cursor)
        Log.add("启用用户" if flag else "禁用用户", user=uid)

    @staticmethod
    @transaction()
    def all(cursor=None):
        users = select(Item().select("users", columns=["id", "flag", "email", "username", "logo", "state"]))(cursor)
        return [Item(id=id, flag=flag, email=email, username=username, logo=logo,state=state) for id, flag, email, username, logo,state in
                users]

    @staticmethod
    @transaction()
    def get_by_id(uid, cursor=None):
        u = select(Item(id=uid).select("users", columns=User._mapper_columns()), one=True)(cursor)
        return User._mapper_row(u)

    @staticmethod
    def _mapper_columns():
        return ["id", "username", "email", "logo", "contact", "state", "flag"]

    @staticmethod
    def _mapper_row(u):
        uid, username, email, logo, contact, state, flag = u
        return Item(id=uid, username=username, email=email, logo=logo, state=state, flag=flag,
                    contact=pickle.loads(contact) if contact else None)


    @staticmethod
    @transaction()
    def get_by_email(email, cursor=None):
        u = select(Item(email=email, flag=UserFlag.EMAIL).select("users", columns=User._mapper_columns()), one=True)(
            cursor)
        return User._mapper_row(u)

    @staticmethod
    @transaction(False)
    def set_state(uid, state, cursor=None):
        User._set_state(uid, state, cursor)

    @staticmethod
    @transaction()
    def check(uid, password, cursor=None):
        row = select(Item(id=uid).select("users", ["password"]), one=True)(cursor)
        return row and _encrypt.check(password, row[0])


    @staticmethod
    @transaction(False)
    def auth_email(email, password, cursor=None):
        cs = ["password"]
        cs.extend(User._mapper_columns())
        row = select(
            Item(flag=UserFlag.EMAIL, email=email).select("users", columns=cs), one=True)(cursor)

        if row:
            user = User._mapper_row(row[1:])
            if _encrypt.check(password, row[0]):

                update(Item(last_login=datetime.datetime.now()).update("users", id_val=user.id))
                Log._add("成功登录", user.id, LogFlag.INFO, cursor)
                return user
            else:
                Log._add("登录验证失败", user.id, LogFlag.INFO, cursor)
        return None

    @staticmethod
    def _get_id_by_email(email, cursor):
        return select(Item(email=email, flag=UserFlag.EMAIL).select("users", ["id"]))(cursor)

    @staticmethod
    def _set_state(uid, state, cursor):
        update(Item(state=state).update("users", id_val=uid))(cursor)

    @staticmethod
    @transaction(False)
    def add_email(username, email, password, cursor=None):
        uid = User._add_email(username, email, password, cursor)
        Log._add("创建用户", uid, LogFlag.INFO, cursor)

    @staticmethod
    def _add_email(username, email, password, cursor):
        return insert(
            Item(username=username, email=email, password=_encrypt.password(password),
                 flag=UserFlag.EMAIL).insert(
                "users"))(cursor)


class Task(object):
    @staticmethod
    def _set_next_run(tid, next_run, cursor):
        update("UPDATE tasks SET next_run_=%s, index_=index_+1 WHERE id_=%s", [next_run, tid])(cursor)

    @staticmethod
    @transaction()
    def list_available(cursor=None):
        ts = select("SELECT id_, flag_, request_ FROM tasks WHERE next_run_<=%s", [datetime.datetime.now()])(cursor)
        return [Task._row_mapper(t) for t in ts]

    @staticmethod
    def _row_mapper(task):
        id, flag, request = task
        return Item(id=id, flag=flag, request=pickle.loads(request))

    @staticmethod
    @transaction()
    def list_by_flag(flag, cursor=None):
        cursor.execute("SELECT id_, flag_, request_ FROM tasks WHERE flag_=%s", [flag])
        ts = cursor.fetchall()
        return [Task._row_mapper(t) for t in ts]


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
    @transaction(readonly=True)
    def get(key, encrypt=False, cursor=None):
        return Setting._get(key, encrypt, cursor)

    @staticmethod
    @transaction()
    def set(key, val, encrypt=False, cursor=None):
        Setting._set(key, val, encrypt, cursor)

    @staticmethod
    def _get(key, encrypt, cursor):
        row = select(Item(key=key).select(name="settings", columns=["val"]), one=True)(cursor)
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
    @transaction(False)
    def add(domain, name, logo, cursor=None):
        return insert(Item(domain=domain, name=name, logo=logo).insert("friend_links"))(cursor)

    @staticmethod
    @transaction(False)
    def set(flid, domain, name, logo, cursor=None):
        update(Item(domain=domain, name=name, logo=logo).update("friend_links", id_val=flid))(cursor)

    @staticmethod
    @transaction()
    def all(cursor=None):
        fls = select(Item().select(name="friend_links", columns=["id", "logo", "name", "domain"]))(cursor)
        return [FriendLink._row_mapper(fl) for fl in fls]

    @staticmethod
    @transaction()
    def get(flid, cursor=None):
        fl = select(Item(id=flid).select(name="friend_links", columns=["id", "logo", "name", "domain"]), one=True)(
            cursor)
        return FriendLink._row_mapper(fl)

    @staticmethod
    @transaction()
    def delete(flid, cursor=None):
        fl = delete(Item(id=flid).delete(name="friend_links"))(cursor)
        return FriendLink._row_mapper(fl)

    @staticmethod
    def _row_mapper(fl):
        id, logo, name, domain = fl
        return Item(id=id, logo=logo, name=name, domain=domain)


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
            Item(role=role, operation=operation, resource=resource).select("permissions", ["begin", "end"]),
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
    @transaction()
    def auth4admin(uid, cursor=None):
        return Permission._auth(role="user://%d" % uid, operation=Operation.MANAGER, resource="SITE",
                                cursor=cursor)


class Log(object):
    @staticmethod
    @transaction()
    def list_range(begin, end, user, limit, cursor=None):
        cursor.execute(
            "SELECT id_, user_, message_, flag_, created_ FROM logs WHERE created_ > %s AND created_ < %s AND user_=%s ORDER BY id_ DESC LIMIT %s",
            [begin, end, user, limit])
        return [Log._row_mapper(i) for i in cursor.fetchall()]

    @staticmethod
    def _row_mapper(l):
        iid, user, message, flag, created = l
        return Item(id=iid, user=user, message=message, flag=flag, created=created)

    @staticmethod
    @transaction()
    def add(message, user=None, flag=LogFlag.INFO, cursor=None):
        Log._add(message, user, flag, cursor)


    @staticmethod
    def _add(message, user, flag, cursor):
        item = Item(message=message, flag=flag)
        if user:
            item.user = user
        insert(item.insert("logs"))(cursor)
