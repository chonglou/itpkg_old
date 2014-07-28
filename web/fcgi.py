#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from webadm import create_app

app = create_app('config.cfg')

from flup.server.fcgi import WSGIServer
WSGIServer(app,bindAddress='/tmp/webadm.sock').run()
  