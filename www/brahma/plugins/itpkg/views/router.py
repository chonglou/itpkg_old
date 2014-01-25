__author__ = 'zhengjitang@gmail.com'

import tornado.web,logging
from brahma.web import Message
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.forms import InfoForm, InitForm
from brahma.plugins.itpkg.store import RouterDao
from brahma.plugins.itpkg.rpc import Rpc


class InitHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_router(rid):
            form = InitForm("init", "量产系统", "/itpkg/%s/init" % rid)
            form.wanDns1.data = "8.8.8.8"
            form.wanDns2.data = "8.8.4.4"
            form.wanNetmask.data = "255.255.255.0"
            form.lanNet.data = "192.168.1.0"
            self.render("itpkg/init.html", form=form)

    @tornado.web.authenticated
    def post(self, rid):
        if self.check_router(rid):
            fm = InitForm(formdata=self.request.arguments)
            messages = []
            if fm.validate():
                if fm.flag.data == "ArchLinux":
                    if not fm.wanMac.data:
                        messages.append("WAN MAC不能为空")
                    if not fm.lanMac.data:
                        messages.append("LAN MAC不能为空")

                    if fm.wanFlag.data == 'static':
                        if not fm.wanIp.data:
                            messages.append("WAN IP不能为空")
                        if not fm.wanNetmask.data:
                            messages.append("WAN 掩码不能为空")
                        if not fm.wanGateway.data:
                            messages.append("WAN 网关不能为空")
                    elif fm.wanFlag.data == 'dhcp':
                        pass
                    else:
                        messages.append("暂不支持的网络类型[%s]" % fm.wanFlag.data)
                    if not messages:
                        host = fm.host.data
                        try:
                            i = host.index(':')
                            port = host[i + 1:]
                            host = host[0:i]
                        except ValueError:
                            port = 22
                        lanNet = fm.lanNet.data
                        lanNet = lanNet[:lanNet.rindex(".")]
                        rpc = Rpc(host=host, port=port)
                        ok, result = rpc.install(
                            wan={
                                "mac": fm.wanMac.data.lower(),
                                "flag": fm.flag.data,
                                "ip": fm.wanIp.data,
                                "netmask": fm.wanNetmask.data,
                                "gateway": fm.wanGateway.data,
                                "dns1": fm.wanDns1.data,
                                "dns2": fm.wanDns2.data,
                            },
                            lan={"mac": fm.lanMac.data.lower(), "net": lanNet}
                        )
                        logging.debug("%s,%s"%(ok, result))
                        if ok:
                            self.render_message_widget(Message(ok=True))
                        else:
                            self.render_message_widget(Message(messages=result))
                        return
                else:
                    messages.append("暂不支持的设备类型[%s]" % fm.flag.data)
            else:
                messages.extend(fm.messages())

            self.render_message_widget(Message(messages=messages))


class RouterHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_router(rid):
            self.render_ctlbar_widget(act="/itpkg/%s" % rid,
                                      items=[
                                          ("wan", "WAN配置"),
                                          ("lan", "LAN配置"),
                                          ("user", "用户"),
                                          ("group", "用户组"),
                                          ("dhcp", "DHCP服务"),
                                          ("dns", "DNS服务"),
                                          ("firewall", "防火墙"),
                                          ("limit", "限速规则"),
                                          ("init", "初始化"),
                                          ("status", "当前状态"),
                                      ])


class ListHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render("itpkg/router.html", routers=RouterDao.all(self.current_user['id']))

    @tornado.web.authenticated
    def post(self):
        fm = InfoForm(formdata=self.request.arguments)

        if fm.validate():
            rid = fm.iid.data
            if rid:
                if self.check_router(rid):
                    RouterDao.set_info(rid, fm.name.data, fm.details.data)
                    self.render_message_widget(Message(ok=True))
            else:
                RouterDao.add(self.current_user['id'], fm.name.data, fm.details.data)
                self.render_message_widget(Message(ok=True))
        else:
            messages = []
            messages.extend(fm.messages())
            self.render_message(Message(messages=messages))

    @tornado.web.authenticated
    def put(self):
        rid = self.get_argument("id", None)
        form = InfoForm("router", "添加路由器", "/itpkg/router")
        if rid:
            if self.check_router(rid):
                form.label = "编辑路由器[%s]" % rid
                r = RouterDao.get(rid)
                form.iid.data = r.id
                form.name.data = r.name
                form.details.data = r.details
                self.render_form_widget(form=form)
        else:
            self.render_form_widget(form=form)


class MainHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render_page("itpkg/main.html", "IT-PACKAGE", index="/itpkg/")


handlers = [
    (r"/itpkg/", MainHandler),
    (r"/itpkg/router", ListHandler),
    (r"/itpkg/router/([0-9]+)", RouterHandler),
    (r"/itpkg/([0-9]+)/init", InitHandler),
]