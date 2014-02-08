__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.forms import WanForm, LanForm
from brahma.plugins.itpkg.store import RouterDao
from brahma.plugins.itpkg.models import WanFlag


class NetworkHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_manager(rid):
            self.render("itpkg/network.html", rid=rid)

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")

            wan, lan = RouterDao.get_network(rid)

            if act == "wan":
                form = WanForm("wan", "WAN 配置", "/itpkg/%s/network" % rid)
                form.act.data = "wan"
                if wan.flag == WanFlag.STATIC:
                    form.ip.data = wan.ip
                    form.netmask.data = wan.netmask
                    form.gateway.data = wan.gateway
                form.dns1.data = wan.dns1
                form.dns2.data = wan.dns2
                self.render_form_widget(form)
            elif act == "lan":
                form = LanForm("lan", "LAN 配置", "/itpkg/%s/network" % rid)
                form.act.data = "lan"
                form.net.data = "%s.0" % lan.net
                form.domain.data = lan.domain

                self.render_form_widget(form)

            else:
                self.render_message_widget(messages=["未知操作"])


    @tornado.web.authenticated
    def post(self, rid):
        if self.check_state(rid):
            from brahma.plugins.itpkg.rpc import create

            act = self.get_argument("act")
            messages = []
            if act == "lan":
                fm = LanForm(formdata=self.request.arguments)
                if fm.validate():
                    net = fm.net.data
                    try:
                        net = net[:net.rindex(".")]
                    except KeyError:
                        messages.append("LAN ID格式不正确")
                    if net:
                        wan, lan = RouterDao.get_network(rid)
                        rpc = create(rid)
                        ok, result = rpc.set_lan(net)
                        if ok:
                            lan.net = net
                            lan.domain = fm.domain.data
                            RouterDao.set_lan(rid, lan)
                            self.render_message_widget(ok=True)
                            return
                        else:
                            messages.extend(result)
                else:
                    messages.append(fm.messages())
            elif act == "wan":
                fm = WanForm(formdata=self.request.arguments)
                if fm.validate():
                    wan, lan = RouterDao.get_network(rid)
                    rpc = create(rid)
                    ok, result = rpc.set_wan(fm.ip.data, fm.netmask.data, fm.gateway.data, fm.dns1.data, fm.dns2.data)
                    if ok:
                        wan.ip = fm.ip.data
                        wan.netmask = fm.netmask.data
                        wan.gateway = fm.gateway.data
                        wan.dns1 = fm.dns1.data
                        wan.dns2 = fm.dns2.data
                        RouterDao.set_wan(rid, wan)
                        self.render_message_widget(ok=True, messages=[
                            "保存新WAN IP成功",
                            "请在物理线路切换之后，手工重启路由器"]
                        )
                        return
                    else:
                        messages.extend(result)
                else:
                    messages.append(fm.messages())

            self.render_message_widget(messages=messages)


handlers = [
    (r"/itpkg/([0-9]+)/network", NetworkHandler),
]