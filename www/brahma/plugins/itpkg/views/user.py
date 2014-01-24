__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.plugins.itpkg.store import UserDao


class UserHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render("itpkg/user.html", users=UserDao.all(self.current_user['id']))


handlers = [
    (r"/itpkg/user", UserHandler),
]