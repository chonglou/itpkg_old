__author__ = 'zhengjitang@gmail.com'

import datetime

from brahma.models import Task
from brahma.env import db_call


class TaskDao:
    @staticmethod
    @db_call
    def available(session=None):
        now = datetime.datetime.now()
        return session.query(Task).filter(Task.nextRun >= now).all()

    @staticmethod
    @db_call
    def set_nextRun(tid, session=None):
        t = session.query(Task).filter(Task.id == tid).one()
        t.index += 1
        t.nextRun = datetime.datetime.now() + datetime.timedelta(
            seconds=t.space) if not t.total or t.index < t.total else datetime.datetime.max


    @staticmethod
    @db_call
    def delete(tid, session=None):
        session.query(Task).filter(Task.id == tid).delete()

    @staticmethod
    @db_call
    def add(flag, space, request=None, begin=datetime.datetime.now(), end=datetime.datetime.max, total=0, session=None):
        session.add(Task(flag, request, begin, end, total, space))

