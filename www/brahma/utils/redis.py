__author__ = 'zhengjitang@gmail.com'

import redis as _redis


class Redis:
    def __init__(self, name, host="localhost", port=6379):
        self.name = name
        self.__pool = _redis.ConnectionPool(host=host, port=port)

    def set(self, key, val=None, fun=None):
        self.__client().set(self.__key(key), fun() if fun else val)

    def get(self, key):
        return self.__client().get(self.__key(key))

    def rpush(self, key, val):
        self.__client().rpush(key, val)

    def blpop(self, key):
        return self.__client().blpop(key)

    def __key(self, key):
        return "{0}://{1}".format(self.name, key)

    def __client(self):
        return _redis.Redis(connection_pool=self.__pool)


