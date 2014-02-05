__author__ = 'zhengjitang@gmail.com'

import logging
import datetime

import tornado.options

from brahma.utils.encrypt import Encrypt
from brahma.utils.redis import Redis
from brahma.utils.ssh import Ssh

ssh = Ssh()

start_stamp = datetime.datetime.now()

encrypt = Encrypt(tornado.options.options.app_secret)


def db_call(func):
    from sqlalchemy.exc import SQLAlchemyError

    def __decorator(*args, **kwargs):
        session = None
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

