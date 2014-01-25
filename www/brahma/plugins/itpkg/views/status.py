__author__ = 'zhengjitang@gmail.com'

from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao


class StatusHandler(BaseHandler):
    def get(self, rid):
        if self.check_manager(rid):
            self.render("itpkg/status.html", router=RouterDao.get(rid))


handlers = [
    (r"/itpkg/([0-9]+)/status", StatusHandler),
]