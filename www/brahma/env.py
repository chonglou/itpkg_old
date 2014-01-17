__author__ = 'zhengjitang@gmail.com'

import logging

import tornado.options

from brahma.utils.cache import Beaker
from brahma.utils.encrypt import Encrypt
from brahma.utils.redis import Redis
from brahma.store import Database

encrypt = Encrypt(tornado.options.options.app_secret)

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


redis = Redis(
    name=tornado.options.options.app_name,
    host=tornado.options.options.redis_host,
    port=tornado.options.options.redis_port
)

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
        return val

    return __decorator
