__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.web

from brahma.views import BaseHandler
from brahma.plugins.wiki.store import WikiDao


class SelfHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        author = self.current_user['id']
        if self.is_admin():
            author = None
        items = WikiDao.list_wiki(datetime.datetime.min, datetime.datetime.max, author=author)

        self.render("wiki/self.html", items=items)


handlers = [(r"/personal/wiki", SelfHandler)]