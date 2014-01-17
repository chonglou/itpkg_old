__author__ = 'zhengjitang@gmail.com'
import tornado.web
from brahma.views import BaseHandler


class UserHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        pass


handlers = [
    (r"/admin/user", UserHandler),
]
