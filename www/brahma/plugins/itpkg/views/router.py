__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler


class RouterHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        self.write("路由器%s" % rid)


class ListHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.write("路由列表")


handlers = [
    (r"/itpkg/router", ListHandler),
    (r"/itpkg/router/([0-9]+)", RouterHandler),
]