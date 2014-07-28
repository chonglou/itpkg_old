#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flaskext.sqlalchemy import SQLAlchemy
from flaskext.cache import Cache

__all__ = ['db', 'cache', 'site_id']

db = SQLAlchemy()
cache = Cache()
site_id = 54321
  