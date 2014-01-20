__author__ = 'zhengjitang@gmail.com'

import logging
import datetime

from sqlalchemy.orm.exc import NoResultFound

from brahma.plugins.wiki.models import Wiki
from brahma.env import db_call


class WikiDao:
    @staticmethod
    @db_call
    def get_wiki(name, session=None):
        try:
            return session.query(Wiki).filter(Wiki.name==name).one()
        except NoResultFound:
            return None

    @staticmethod
    @db_call
    def save_wiki(name, title, body, author=None, session=None):
        try:
            w = session.query(Wiki).filter(Wiki.name==name).one()
        except NoResultFound:
            w = None
        if w:
            w.title = title
            w.body = body
            w.lastEdit = datetime.datetime.now()
        else:
            w = Wiki(name, title, body, author)
            session.add(w)

    @staticmethod
    @db_call
    def del_wiki(name, session=None):
        try:
            w = session.query(Wiki).filter(Wiki.name==name).one()
        except NoResultFound:
            logging.error("知识库[%s]不存在" % name)
            return
        session.delete(w)

    @staticmethod
    @db_call
    def list_wiki(begin, end, session=None):
        return session.query(Wiki).filter(Wiki.created >= begin, Wiki.created <= end).all()

