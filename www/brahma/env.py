__author__ = 'zhengjitang@gmail.com'

import logging,datetime

import tornado.options

from brahma.utils.encrypt import Encrypt
from brahma.utils.redis import Redis
from brahma.store import Database

start_stamp = datetime.datetime.now()

encrypt = Encrypt(tornado.options.options.app_secret)


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

"""
cache = Beaker(tornado.options.options.app_name, tornado.options.options.app_store)
def cache_call(key):
    def _decorator(func):
        def __decorator(*args, **kwargs):
            val = cache.get(key)
            if val:
                return val
            val = func(*args, **kwargs)
            cache.set(key, val)
            return val

        return __decorator

    return _decorator
"""

_db = Database(
    tornado.options.options.db_uri,
    tornado.options.options.debug,
    plugins=tornado.options.options.app_plugins
)


def db_call(func):
    from sqlalchemy.exc import SQLAlchemyError

    def __decorator(*args, **kwargs):
        session = _db.session()
        kwargs['session'] = session
        val = None
        try:
            val = func(*args, **kwargs)
            session.commit()
        except SQLAlchemyError:
            session.rollback()
            logging.exception("数据库操作出错")

        session.close()
        return val

    return __decorator


redis = Redis(
    name=tornado.options.options.app_name,
    host=tornado.options.options.redis_host,
    port=tornado.options.options.redis_port
)

