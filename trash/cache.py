__author__ = 'zhengjitang@gmail.com'

import logging


class Beaker:
    def __init__(self, name, path):
        opts = {
            'cache.type': 'file',
            'cache.data_dir': path + "/cache/data",
            'cache.lock_dir': path + "/cache/lock",
        }
        from beaker.cache import CacheManager
        from beaker.util import parse_cache_config_options

        manager = CacheManager(**parse_cache_config_options(opts))
        self.__cache = manager.get_cache(name)

    def set(self, key, val):
        self.__cache.put(key, val)

    def clear(self):
        self.__cache.clear()

    def pop(self, key):
        self.__cache.remove_value(key)

    def get(self, key):
        try:
            return self.__cache.get(key)
        except KeyError:
            logging.error("缓存[%s]不存在", key)
            return None



