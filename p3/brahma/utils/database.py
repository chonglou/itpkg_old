__author__ = 'zhengjitang@gmail.com'

import mysql.connector
from mysql.connector.pooling import MySQLConnectionPool


def bulk(lines):
    def query(cursor):
        for line in lines:
            cursor.execute(*line)

    return query


def update(line):
    return lambda cursor: cursor.execute(*line)


def count(line):
    def query(cursor):
        cursor.execute(*line)
        row = cursor.fetchone()
        return row[0]

    return query


def delete(line):
    return lambda cursor: cursor.execute(*line)


def select(line, one=False):
    def query(cursor):
        cursor.execute(*line)
        result = cursor.fetchall()
        if one:
            if result:
                if len(result) == 1:
                    return result[0]
                raise Exception("多条记录")
            return None
        else:
            return result
    return query


def insert(line):
    def query(cursor):
        cursor.execute(*line)
        return cursor.lastrowid

    return query


class Mysql(object):
    def __init__(self, name, tables=list(), password=None, host="localhost", port=3306, user="root", pool_size=5,
                 init=False, echo=False):
        if echo:
            self.__init_logging()

        args = {
            "user": user,
            "password": password,
            "port": port,
            "host": host,
        }
        self.__database = name
        if init:
            self.__check_database(name=name, tables=tables, **args)

        self.__pool = MySQLConnectionPool(pool_name=name, pool_size=pool_size, database=name, **args)

    def __init_logging(self):
        import logging

        logger = logging.getLogger("sql")

        def warp(func):
            def wrapped_func(*args, **kwargs):
                logger.debug("[%s]" % args[1])
                return func(*args, **kwargs)

            return wrapped_func

        from mysql.connector.cursor import MySQLCursor

        def warp_functions(clazz, method):
            setattr(clazz, method, warp(getattr(clazz, method)))

        warp_functions(MySQLCursor, "execute")

    def __check_database(self, name, tables, **kwargs):
        cnx = None
        try:
            cnx = mysql.connector.connect(**kwargs)
            cursor = cnx.cursor()
            sql = "CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARACTER SET 'utf8'" % name

            cursor.execute(sql)
            cnx.database = name
            for name, key, created, version, columns in tables:
                sql = "CREATE TABLE IF NOT EXISTS %s(%s%s%s%s%s) ENGINE=InnoDB" % (
                    name,
                    "id_ INTEGER NOT NULL AUTO_INCREMENT, " if key else "",
                    ', '.join(columns),
                    ", created_ TIMESTAMP NOT NULL DEFAULT NOW()" if created else "",
                    ", version_ INTEGER NOT NULL DEFAULT 0" if version else "",
                    ", PRIMARY KEY (id_)" if key else ""
                )

                cursor.execute(sql)
            cursor.close()
        finally:
            if cnx:
                cnx.close()

    @property
    def connection(self):
        return self.__pool.get_connection()

    def close(self):
        self.__pool._remove_connections()
