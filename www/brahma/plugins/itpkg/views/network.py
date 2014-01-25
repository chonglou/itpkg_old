__author__ = 'zhengjitang@gmail.com'

import logging

import tornado.web

from brahma.web import Message
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.forms import WanForm,LanForm
from brahma.plugins.itpkg.rpc import Rpc
from brahma.plugins.itpkg.store import RouterDao


class NetworkHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_manager(rid):
            self.render("itpkg/network.html", rid=rid)

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")
            import json
            router = RouterDao.get(rid)

            if act == "wan":
                form = WanForm("wan", "WAN 配置", "/itpkg/%s/network" % rid)
                form.act.data = "wan"

                wan = json.loads(router.wan)
                if wan['flag'] == 'static':
                        form.ip.data = wan['ip']
                        form.netmask.data = wan['netmask']
                        form.gateway.data = wan['gateway']
                form.dns1.data = wan['dns1']
                form.dns2.data = wan['dns2']
                self.render_form_widget(form)
            elif act == "lan":
                form = LanForm("lan", "LAN 配置", "/itpkg/%s/network" % rid)
                form.act.data = "lan"
                lan = json.loads(router.lan)
                form.net.data = lan['net']
                form.domain.data = lan['domain']

                self.render_form_widget(form)

            elif act == "apply":
                pass
            else:
                self.render_message_widget(messages=["未知操作"])



    @tornado.web.authenticated
    def post(self, rid):
        if self.check_state(rid):
            import  json
            act = self.get_argument("act")
            messages = []
            router = RouterDao.get(rid)
            if act == "lan":
                fm = LanForm(formdata=self.request.arguments)
                if fm.validate():
                    lan = json.loads(router.lan)
                    lan['net'] = fm.net.data
                    lan['domain'] = fm.domain.data
                    RouterDao.set_lan(rid, **lan)
                    self.render_message_widget(ok=True)
                    return
                else:
                    messages.append(fm.messages())
            elif act == "wan":
                fm = WanForm(formdata=self.request.arguments)
                if fm.validate():
                    wan = json.loads(router.wan)
                    wan['ip'] = fm.ip.data
                    wan['netmask'] = fm.netmask.data
                    wan['gateway'] = fm.gateway.data
                    wan['dns1'] = fm.dns1.data
                    wan['dns2'] = fm.dns2.data
                    RouterDao.set_wan(rid, **wan)
                    self.render_message_widget(ok=True)
                    return
                else:
                    messages.append(fm.messages())

            self.render_message_widget(messages=messages)

handlers = [
    (r"/itpkg/([0-9]+)/network", NetworkHandler),
]