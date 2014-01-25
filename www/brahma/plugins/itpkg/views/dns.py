__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler


class DnsHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        #todo
        pass

    @tornado.web.authenticated
    def post(self, rid):
        #todo
        pass


handlers = [
    (r"/itpkg/([0-9]+)/dns", DnsHandler),
]