__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.views import BaseHandler


class ManagerHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, act):
        if act == "tag":
            self.write("tags")


class MainHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render("itpkg/self.html")


handlers = [
    (r"/personal/itpkg", MainHandler),
]