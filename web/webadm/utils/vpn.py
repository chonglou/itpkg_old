#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

import MySQLdb

class VpnUser:
    def __init__(self, name, password=None, active=0):
        self.name = name
        self.password = password
        self.active = active

    def __repr__(self):
        return u"<VPN ('%s', '%s')>" % (self.name, self.active)


class VpnMysqlHelper:
    create_table_sql = "CREATE TABLE vpnuser (name char(20) NOT NULL, password char(128) default NULL, active int(10) NOT NULL DEFAULT 1, PRIMARY KEY (name))"

    def __init__(self, host, user, db, password):
        try:
            self.db = MySQLdb.connect(host=host, user=user, db=db, passwd=password, port=3306)
        except MySQLdb, e:
            self.db = None
            raise Exception(e)

    def close(self):
        if self.db:
            self.db.close()

    def delete_vpn_user(self, name):
        c = self.db.cursor()
        c.execute("DELETE FROM vpnuser WHERE name='%s'" % name)
        c.close()
        self.db.commit()


    def select_vpn_user(self, name):
        rv = None
        c = self.db.cursor()
        c.execute("SELECT name, active FROM vpnuser WHERE name='%s'" % name)
        item = c.fetchone()
        if item:
            rv = VpnUser(item[0], active=item[1])
            c.close()
        return rv

    def list_all_vpn_users(self):
        rv = list()
        c = self.db.cursor()
        c.execute('SELECT name, active FROM vpnuser WHERE 1=1')
        for item in c.fetchall():
            rv.append(VpnUser(item[0], active=item[1]))
        c.close()
        return rv

    def store_vpn_user(self, user):
        c = self.db.cursor()
        if self.select_vpn_user(user.name):
            c.execute("UPDATE vpnuser SET name='%s',password=password('%s'), active=%s WHERE name='%s'" %
                      ( user.name, user.password, user.active, user.name))
        else:
            c.execute("INSERT INTO vpnuser (name, password, active) VALUES('%s',password('%s'), %s)" %
                      (user.name, user.password, user.active))
        c.close()
        self.db.commit()


    #    def insert_vpn_user(self, user):
    #        c = self.db.cursor()
    #        c.execute("INSERT INTO vpnuser (name, password, active) VALUES('%s',password('%s'), %s)" %
    #                  (user.name, user.password, user.active))
    #       c.close()
    #       self.db.commit()

    #    def update_vpn_user(self, name, user):
    #        c = self.db.cursor()
    #        c.execute("UPDATE vpnuser SET name='%s',password=password('%s'), active=%s WHERE name='%s'" %
    #                  ( user.name, user.password, user.active, name))
    #        c.close()
    #        self.db.commit()


    def create_vpn_table(self):
        c = self.db.cursor()
        c.execute(self.create_table_sql)
        c.close()

    def drop_vpn_table(self):
        c = self.db.cursor()
        c.execute("DROP TABLE vpnuser")
        c.close()

    def count_enable_vpn_users(self):
        c = self.db.cursor()
        c.execute("SELECT COUNT(*) FROM vpnuser WHERE active=1")
        rv = c.fetchone()[0]
        c.close()
        return rv

    def count_disable_vpn_users(self):
        c = self.db.cursor()
        c.execute("SELECT COUNT(*) FROM vpnuser WHERE active!=1")
        rv = c.fetchone()[0]
        c.close()
        return rv

    def count_all_vpn_users(self):
        c = self.db.cursor()
        c.execute("SELECT COUNT(*) FROM vpnuser WHERE 1=1")
        rv = c.fetchone()[0]
        c.close()
        return rv
  


  