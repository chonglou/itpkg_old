__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.plugins.itpkg.store import GroupDao


class GroupHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render("itpkg/group.html", groups=GroupDao.all(self.current_user['id']))


handlers = [
    (r"/itpkg/group", GroupHandler),
]