__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao


class DnsHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            self.render("itpkg/dns.html", rid=rid)

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")
            from brahma.plugins.itpkg.rpc import create
            r = RouterDao.get(rid)
            rpc = create(rid)
            if act == "apply":
                import json
                lan = json.loads(r.lan)
                wan = json.loads(r.wan)
                ok, result = rpc.apply_named(lan['net'], wan['dns1'], wan['dns2'])
                if ok:
                    self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=result)
            elif act == "status":
                ok, result = rpc.status_named()
                self.render_message_widget(ok=ok, messages=result)
            else:
                self.render_message_widget(messages=["未知操作"])


handlers = [
    (r"/itpkg/([0-9]+)/dns", DnsHandler),
]