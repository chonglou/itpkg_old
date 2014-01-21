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
            return session.query(Wiki).filter(Wiki.name == name).one()
        except NoResultFound:
            return None

    @staticmethod
    @db_call
    def add_wiki(name, title, body, author, session=None):
        w = Wiki(name, title, body, author)
        session.add(w)

    @staticmethod
    @db_call
    def set_wiki(name, title, body, session=None):
        w = session.query(Wiki).filter(Wiki.name == name).one()
        w.title = title
        w.body = body
        w.lastEdit = datetime.datetime.now()


    @staticmethod
    @db_call
    def del_wiki(name, session=None):
        try:
            w = session.query(Wiki).filter(Wiki.name == name).one()
        except NoResultFound:
            logging.error("知识库[%s]不存在" % name)
            return
        session.delete(w)

    @staticmethod
    @db_call
    def list_wiki(begin, end, author=None, session=None):
        if author:
            return session.query(Wiki).filter(Wiki.author == author, Wiki.created >= begin, Wiki.created <= end).all()
        return session.query(Wiki).filter(Wiki.created >= begin, Wiki.created <= end).order_by(
            Wiki.created.desc()).all()

