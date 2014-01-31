__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao, DeviceDao
from brahma.plugins.itpkg.forms import DeviceBindForm


class DhcpHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            import json

            r = RouterDao.get(rid)
            net = json.loads(r.lan)['net']
            form = DeviceBindForm("bind", "IP绑定", "/itpkg/%s/dhcp" % rid)
            form.ip.choices = [(i, "%s.%s" % (net, i)) for i in range(2, 254)]
            form.mac.choices = [(d.id, d.mac) for d in DeviceDao.all(rid)]
            form.flag.data = True
            self.render("itpkg/dhcp.html", rid=rid, net=net, form=form, devices=DeviceDao.all_fix(rid))


    @tornado.web.authenticated
    def post(self, rid):
        if self.check_state(rid):
            import json

            r = RouterDao.get(rid)
            net = json.loads(r.lan)['net']
            fm = DeviceBindForm(formdata=self.request.arguments)
            fm.ip.choices = [( i, "%s.%s" % (net, i)) for i in range(2, 254)]
            fm.mac.choices = [(d.id, d.mac) for d in DeviceDao.all(rid)]
            msg = []
            if fm.validate():
                if (not fm.flag.data) or (not DeviceDao.is_ip_inuse(rid, fm.ip.data)):
                    DeviceDao.bind(fm.mac.data, fm.ip.data, fm.flag.data)
                    self.render_message_widget(ok=True)
                    return
                else:
                    msg.append("IP[%s.%s]被占用" % (net, fm.ip.data))
            else:
                msg.extend(fm.messages())
            self.render_message_widget(messages=msg)

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
                #todo test
                ok, result = rpc.apply_dhcpd(lan['domain'], lan['net'], [(d.mac, d.ip) for d in DeviceDao.all_fix(rid)])
                if ok:
                    self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=result)
            elif act == "status":
                ok, result = rpc.status_dhcpd()
                self.render_message_widget(ok=ok, messages=result)
            else:
                self.render_message_widget(messages=["未知操作"])



handlers = [
    (r"/itpkg/([0-9]+)/dhcp", DhcpHandler),
]