__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.options

from brahma.utils.encrypt import Encrypt
from brahma.utils.redis import Redis
from brahma.utils.ssh import Ssh

ssh = Ssh()

start_stamp = datetime.datetime.now()

encrypt = Encrypt(tornado.options.options.app_secret)


def _get_db():
    from brahma.database import Mysql
    args = dict()
    for k in ["host", "name", "user", "port", "password", "pool_size"]:
        args[k] = getattr(tornado.options.options, "mysql_"+k)

    import importlib
    tables = list()
    tables.extend(importlib.import_module("brahma.models").tables)
    from brahma.utils import walk_plugin
    walk_plugin(lambda p:tables.extend(importlib.import_module("brahma.plugins." + p + ".models").tables))

    return Mysql(init=True, tables=tables, **args)

_db = _get_db()


def db_call(func):
    def __decorator(*args, **kwargs):
        val = None
        cnx = _db.connection

        try:

            kwargs['cnx'] = cnx
            val = func(*args, **kwargs)
            cnx.commit()
        except Exception:
            if cnx:
                cnx.rollback()
            import logging
            logging.exception("数据库操作出错")

        finally:
            cnx.close()

        return val

    return __decorator


def _get_cache(path):
    opts = {
        'cache.type': 'file',
        'cache.data_dir': path + "/cache/data",
        'cache.lock_dir': path + "/cache/lock",
    }
    from beaker.cache import CacheManager
    from beaker.util import parse_cache_config_options

    return CacheManager(**parse_cache_config_options(opts))


cache = _get_cache(tornado.options.options.app_store)

redis = Redis(
    name=tornado.options.options.app_name,
    host=tornado.options.options.redis_host,
    port=tornado.options.options.redis_port
)

