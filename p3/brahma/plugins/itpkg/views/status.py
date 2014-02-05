__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao


class StatusHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_manager(rid):
            self.render("itpkg/status.html", router=RouterDao.get(rid), rid=rid)

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_manager(rid):
            from brahma.plugins.itpkg.rpc import create

            rpc = create(rid)
            ok, result = rpc.status()
            self.render_message_widget(ok=ok, messages=result)


handlers = [
    (r"/itpkg/([0-9]+)/status", StatusHandler),
]