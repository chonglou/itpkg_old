#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

import os
from flaskext.script import Server, Shell, Manager, Command, prompt_bool

from webadm import create_app
from webadm.extensions import db,site_id

from webadm.models.dhcpd import *
from webadm.models.named import *
from webadm.models.employee import *
from webadm.models.firewall import *
from webadm.models.mail import *
from webadm.models.host import *
from webadm.models.site import *
from webadm.models.vpn import *

manager = Manager(create_app("%s/config.cfg" % os.getcwd()))
manager.add_command("runserver", Server('0.0.0.0',port=8080))

def _make_context():
    return dict(db=db)
manager.add_command("shell", Shell(make_context=_make_context))

@manager.command
def createall():
    u"创建数据库表"
    db.create_all()
    
@manager.command
def dropall():
    u"删除数据库表"
    if prompt_bool("Are you sure ? You will lose all your data !"):
        db.drop_all()

@manager.command
def initdb():
    u"初始化数据库内容"
    site = Site(u'LZL98网络运维管理系统', admin_user='flamen', admin_email='2682287010@qq.com', copy_right='Copyright &copy; 2011-2016')
    site.id = site_id
    db.session.add(site)
    db.session.add(User('root', '123456'))
    db.session.add(User('xcs', '123456'))
    db.session.commit()

#@manager.option('-n', '--name', dest='name', default='root')
#@manager.option('-p', '--password', dest='password', default='123456')
#def create_user(name, password):
#    u"创建管理员"
#    u = User(name, password)
    

if __name__ == "__main__":
    manager.run()
    


  