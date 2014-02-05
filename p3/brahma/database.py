__author__ = 'zhengjitang@gmail.com'

import mysql.connector
from mysql.connector.pooling import MySQLConnectionPool


class Mysql(object):
    def bulk(self, lines):
        def query(cursor):
            for sql, items in lines:
                self.__logger.debug(sql)
                cursor.execute(sql, items)

        self.__query(query, read=False)

    def update(self, sql, items=None):
        def query(cursor):
            self.__logger.debug(sql)
            cursor.execute(sql, items)

        self.__query(query, read=False)

    def count(self, sql, items=None):
        def query(cursor):
            self.__logger.debug(sql)
            cursor.execute(sql, items)
            row = cursor.fetchone()
            return row[0]

        return self.__query(query)

    def delete(self, sql, items=None):
        def query(cursor):
            self.__logger.debug(sql)
            cursor.execute(sql, items)

        self.__query(query, read=False)

    def select(self, sql, items=None, one=False):
        def query(cursor):
            self.__logger.debug(sql)
            rs = list()
            cursor.execute(sql, items)
            for record in cursor:
                rs.append(record)
            return rs

        result = self.__query(query)
        if one:
            if result:
                if len(result) == 1:
                    return result[0]
                raise Exception("多条记录")
            return None
        else:
            return result

    def insert(self, sql, items):
        def query(cursor):
            self.__logger.debug(sql)
            cursor.execute(sql, items)
            return cursor.lastrowid

        return self.__query(query, read=False)

    def __query(self, query, read=True):
        cnx = None
        try:
            cnx = self.connection
            cursor = cnx.cursor()
            result = query(cursor)
            if not read:
                cnx.commit()
            cursor.close()
            return result
        except Exception as e:
            if not read and cnx:
                cnx.rollback()
            raise e
        finally:
            if cnx:
                cnx.close()

    def close(self):
        self.__pool._remove_connections()

    def __init__(self, name, tables=list(), password=None, host="localhost", port=3306, user="root", pool_size=5,
                 init=False):
        import logging

        self.__logger = logging.getLogger("sql")

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

    def __check_database(self, name, tables, **kwargs):
        cnx = None
        try:
            cnx = mysql.connector.connect(**kwargs)
            cursor = cnx.cursor()
            sql = "CREATE DATABASE IF NOT EXISTS %s DEFAULT CHARACTER SET 'utf8'" % name
            self.__logger.debug(sql)
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
                self.__logger.debug(sql)
                cursor.execute(sql)
            cursor.close()
        finally:
            if cnx:
                cnx.close()

    @property
    def connection(self):
        return self.__pool.get_connection()
