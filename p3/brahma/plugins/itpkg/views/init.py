__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.forms import InitForm
from brahma.plugins.itpkg.store import RouterDao
from brahma.plugins.itpkg.models import WanFlag, RouterFlag, Wan, Lan
from brahma.models import State


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
        if fm.wanFlag.data == WanFlag.STATIC:
            if not fm.wanIp.data:
                messages.append("WAN IP不能为空")
            if not fm.wanNetmask.data:
                messages.append("WAN 掩码不能为空")
            if not fm.wanGateway.data:
                messages.append("WAN 网关不能为空")
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
            if RouterDao.get_state(rid) != State.SUBMIT:
                self.render_message_widget(messages=['已经初始化'])
                return

            fm = InitForm(formdata=self.request.arguments)
            messages = []

            if fm.validate():
                if fm.flag.data == RouterFlag.ARCH_LINUX_OLD:
                    if getattr(self, "_init_%s" % fm.flag.data.lower())(rid, fm, messages):
                        return
                else:
                    messages.append("暂不支持的设备类型[%s]" % fm.flag.data)
            else:
                messages.extend(fm.messages())

            self.render_message_widget(messages=messages)

    def _init_a(self, rid, fm, messages):
        self.__check_wan(fm, messages)
        lanNet = self.__get_lan_net(fm, messages)
        if not fm.wanMac.data:
            messages.append("WAN MAC不能为空")
        if not fm.lanMac.data:
            messages.append("LAN MAC不能为空")
        if not messages:
            wan = Wan(
                flag=fm.wanFlag.data,
                mac=fm.wanMac.data.lower(),
                ip=fm.wanIp.data,
                username=fm.wanUsername.data,
                password=fm.wanPassword.data,
                netmask=fm.wanNetmask.data,
                gateway=fm.wanGateway.data,
                dns1=fm.wanDns1.data,
                dns2=fm.wanDns2.data)

            lan = Lan(
                mac=fm.lanMac.data.lower(),
                net=lanNet,
                domain=fm.lanDomain.data)

            RouterDao.init(rid, wan=wan, lan=lan, flag=fm.flag.data)
            self.render_message_widget(ok=True)
            return True

        return False


handlers = [
    (r"/itpkg/([0-9]+)/init", InitHandler),
]