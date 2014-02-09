__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao, DeviceDao
from brahma.plugins.itpkg.forms import DhcpForm, ip_choices
from brahma.models import State


class DhcpHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            wan, lan = RouterDao.get_network(rid)
            form = DhcpForm("bind", "IP绑定", "/itpkg/%s/dhcp" % rid)
            form.ip.choices = ip_choices(lan.net)
            form.mac.choices = DeviceDao.choices(rid, State.ENABLE)
            form.flag.data = True
            self.render("itpkg/dhcp.html", rid=rid, net=lan.net, form=form, devices=DeviceDao.list_all_fix(rid))


    @tornado.web.authenticated
    def post(self, rid):
        if self.check_state(rid):

            wan, lan = RouterDao.get_network(rid)
            fm = DhcpForm(formdata=self.request.arguments)
            fm.ip.choices = ip_choices(lan.net)
            fm.mac.choices = DeviceDao.choices(rid, State.ENABLE)
            msg = []
            if fm.validate():
                if (not fm.flag.data) or (not DeviceDao.is_ip_inuse(rid, fm.ip.data)):
                    DeviceDao.set_fix(fm.mac.data, fm.ip.data, fm.flag.data)
                    self.render_message_widget(ok=True)
                    return
                else:
                    msg.append("IP[%s.%s]被占用" % (lan.net, fm.ip.data))
            else:
                msg.extend(fm.messages())
            self.render_message_widget(messages=msg)

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")
            from brahma.plugins.itpkg.rpc import create

            if act == "apply":
                #todo test
                rpc = create(rid)
                wan, lan = RouterDao.get_network(rid)
                ok, result = rpc.apply_dhcpd(lan.domain, lan.net, DeviceDao.mac_ip_all_fix(rid))
                if ok:
                    self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=result)
            elif act == "status":
                rpc = create(rid)
                ok, result = rpc.status_dhcpd()
                self.render_message_widget(ok=ok, messages=result)
            else:
                self.render_message_widget(messages=["未知操作"])


handlers = [
    (r"/itpkg/([0-9]+)/dhcp", DhcpHandler),
]