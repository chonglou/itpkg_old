__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.forms import InitForm
from brahma.plugins.itpkg.store import RouterDao
from brahma.plugins.itpkg.rpc import Rpc


class InitHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_manager(rid):
            form = InitForm("init", "初始化路由[" + rid + "]", "/itpkg/%s/init" % rid)
            form.wanNetmask.data = "255.255.255.0"
            form.wanDns1.data = "8.8.8.8"
            form.wanDns2.data = "8.8.4.4"
            form.lanNet.data = "192.168.1.0"
            self.render("itpkg/init.html", form=form)

    def __check_wan(self, fm, messages):
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

    def __get_lan_net(self, fm, messages):
        lanNet = fm.lanNet.data
        try:
            return lanNet[:lanNet.rindex(".")]
        except KeyError:
            messages.append("LAN ID格式不正确")

        return None

    def __get_host_port(self, fm):
        host = fm.host.data
        try:
            i = host.index(':')
            port = host[i + 1:]
            host = host[0:i]
        except ValueError:
            port = 22
        return host, port

    @tornado.web.authenticated
    def post(self, rid):
        if self.check_manager(rid):
            r = RouterDao.get(rid)
            if r.state != "SUBMIT":
                self.render_message_widget(messages=['已经初始化'])
                return

            fm = InitForm(formdata=self.request.arguments)
            messages = []

            if fm.validate():
                self.__check_wan(fm, messages)
                lanNet = self.__get_lan_net(fm, messages)
                host, port = self.__get_host_port(fm)

                if fm.flag.data == "ArchLinux":
                    if not fm.wanMac.data:
                        messages.append("WAN MAC不能为空")
                    if not fm.lanMac.data:
                        messages.append("LAN MAC不能为空")

                    from brahma.plugins.itpkg.rpc import ArchLinux

                    if fm.wanFlag.data == 'static':
                        f_wan = ArchLinux.wan_static(
                            ip=fm.wanIp.data,
                            netmask=fm.wanNetmask.data,
                            gateway=fm.wanGateway.data,
                            dns1=fm.wanDns1.data, dns2=fm.wanDns2.data)
                    elif fm.wanFlag.data == 'dhcp':
                        f_wan = ArchLinux.wan_dhcp(fm.wanDns1.data, fm.wanDns2.data)
                    else:
                        messages.append("错误的WAN网络类型")
                        f_wan = None

                    if not messages:
                        rpc = Rpc(host=host, port=port, flag="ArchLinux")
                        ok, result = rpc.call([
                            ArchLinux.udev(fm.wanMac.data.lower(), fm.lanMac.data.lower()),
                            f_wan,
                            ArchLinux.lan(lanNet),
                        ])
                        if ok:
                            if fm.wanFlag.data == "static":
                                RouterDao.set_wan(rid,
                                                  flag="static",
                                                  mac=fm.wanMac.data.lower(),
                                                  ip=fm.wanIp.data,
                                                  netmask=fm.wanNetmask.data,
                                                  gateway=fm.wanGateway.data,
                                                  dns1=fm.wanDns1.data,
                                                  dns2=fm.wanDns2.data)
                            elif fm.wanFlag.data == "dhcp":
                                RouterDao.set_wan(rid,
                                                  mac=fm.wanMac.data.lower(),
                                                  flag="dhcp")
                            RouterDao.set_lan(rid,
                                              mac=fm.lanMac.data.lower(),
                                              net=lanNet,
                                              domain=fm.lanDomain.data)
                            RouterDao.set_state(rid, flag=fm.flag.data, state="ENABLE")
                            self.render_message_widget(ok=True)
                            return
                        else:
                            messages.append(result)
                else:
                    messages.append("暂不支持的设备类型[%s]" % fm.flag.data)
            else:
                messages.extend(fm.messages())

            self.render_message_widget(messages=messages)


handlers = [
    (r"/itpkg/([0-9]+)/init", InitHandler),
]