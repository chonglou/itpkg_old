__author__ = 'zhengjitang@gmail.com'

import datetime

from brahma.models import Task
from brahma.env import db_call


class TaskDao:
    @staticmethod
    @db_call
    def available(session=None):
        now = datetime.datetime.now()
        return session.query(Task).filter(Task.nextRun <= now).all()

    @staticmethod
    @db_call
    def set_nextRun(tid, nextRun=None, session=None):
        t = session.query(Task).filter(Task.id == tid).one()
        if not nextRun:
            t.index += 1
            nextRun = datetime.datetime.now() + datetime.timedelta(
            seconds=t.space) if (not t.total or t.index < t.total) else datetime.datetime.max
        t.nextRun = nextRun

    @staticmethod
    @db_call
    def delete(tid, session=None):
        session.query(Task).filter(Task.id == tid).delete()

    @staticmethod
    @db_call
    def list_by_flag(flag, session=None):
        return session.query(Task).filter(Task.flag == flag).all()

    @staticmethod
    @db_call
    def add(flag, space, request=None, begin=None, end=datetime.datetime.max, total=0,
            nextRun=None, session=None):
        if not begin:
            begin = datetime.datetime.now()
        if not nextRun:
            nextRun = datetime.datetime.now()
        session.add(Task(flag, request, begin, end, total, space, nextRun))

