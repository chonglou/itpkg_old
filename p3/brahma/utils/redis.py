__author__ = 'zhengjitang@gmail.com'

import pickle

import redis as _redis


class Redis:
    def __init__(self, name, host="localhost", port=6379):
        self.name = name
        self.__pool = _redis.ConnectionPool(host=host, port=port)

    def set(self, key, val):
        self.__client().set(self.__key(key), pickle.dumps(val))

    def get(self, key):
        return pickle.loads(self.__client().get(self.__key(key)))

    def lpush(self, key, val):
        self.__client().lpush(self.__key(key), pickle.dumps(val))

    def brpop(self, key):
        k, v = self.__client().brpop(self.__key(key))
        return pickle.loads(v)

    def __key(self, key):
        return "{0}://{1}".format(self.name, key)

    def __client(self):
        return _redis.Redis(connection_pool=self.__pool)


