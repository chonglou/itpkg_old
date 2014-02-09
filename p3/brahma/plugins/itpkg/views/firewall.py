__author__ = 'zhengjitang@gmail.com'

import tornado.web,datetime

from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao, InputDao, OutputDao, NatDao, OutputDeviceDao, DeviceDao
from brahma.plugins.itpkg.forms import InputForm, OutputForm, NatForm, ip_choices
from brahma.plugins.itpkg.models import Protocol
from brahma.models import enum2str

def format_time(t):
    return (datetime.datetime.min+t).time().strftime("%H:%M")

class FirewallHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            wan, lan = RouterDao.get_network(rid)
            def weekdays_cn(i):
                return "[%s-%s]@(%s)"%(format_time(i.begin),format_time(i.end),i.weekdays)
            self.render("itpkg/firewall.html", rid=rid, net=lan.net,
                        enum2str=enum2str,
                        weekdays_cn=weekdays_cn,
                        protocol=Protocol,
                        inputs=InputDao.all(rid),
                        outputs=OutputDao.all(rid),
                        nats=NatDao.all(rid))

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")

            from brahma.plugins.itpkg.rpc import create

            rpc = create(rid)
            wan, lan = RouterDao.get_network(rid)
            if act == "apply":

                def enum2protocol(p):
                    if p==Protocol.TCP:
                        return "tcp"
                    elif p==Protocol.UDP:
                        return "udp"
                    else:
                        return None

                ins = []
                for i in InputDao.all(rid):
                    ins.append((enum2protocol(i.protocol), i.port))

                outs = []
                for o in OutputDao.all(rid):
                    #todo 当前只支持关键字
                    ms = []
                    for od in OutputDeviceDao.list_device(o.id):
                        ms.append(DeviceDao.get_mac(od.device))

                    outs.append(
                        (o.keyword, o.begin, o.end, o.weekdays, ms))

                nats = []
                for n in NatDao.all(rid):
                    nats.append((n.sport, enum2protocol(n.protocol), n.dip, n.dport))

                macs = DeviceDao.list_enable_mac(rid)
                ok, result = rpc.apply_firewall(net=lan.net, ins=ins, outs=outs, nats=nats, macs=macs)
                if ok:
                    self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=result)
            elif act == "clear":
                ok, result = rpc.clear_firewall(lan.net)
                self.render_message_widget(ok=ok)
            elif act == "status":
                ok, result = rpc.status_firewall()
                self.render_message_widget(ok=ok)
            else:
                self.render_message_widget(messages=["未知操作"])


class NatHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid, nid=None):
        if self.check_state(rid):
            form = NatForm("nat", "添加映射规则", "/itpkg/%s/nat/" % rid, scroll=True)
            if nid:
                if not self.__check(rid, nid):
                    return
                n = NatDao.get(nid)
                form.id.data = n.id
                form.dip.data = n.dip
                form.dport.data = n.dport
                form.protocol.data = n.protocol
                form.sport.data = n.sport
                form.name.data = n.name
                form.label = "编辑映射规则[%s]" % n.id
            else:
                form.protocol.data = Protocol.TCP

            self.__set_ip_choices(rid, form)
            self.render_form_widget(form=form)


    @tornado.web.authenticated
    def post(self, rid, nid=None):
        if self.check_state(rid):
            fm = NatForm(formdata=self.request.arguments)
            self.__set_ip_choices(rid, fm)
            if fm.validate():
                if InputDao.is_exist(rid,fm.sport.data, fm.protocol.data) or NatDao.is_exist(rid, fm.sport.data, fm.protocol.data):
                    self.render_message_widget(messages=["规则已存在或端口已占用"])
                else:
                    if fm.id.data:
                        nid = fm.id.data
                        if not self.__check(rid, nid):
                            return
                        NatDao.set(nid, fm.name.data, fm.sport.data, fm.protocol.data, fm.dip.data, fm.dport.data)
                        self.render_message_widget(ok=True)
                    else:
                        NatDao.add(rid, fm.name.data, fm.sport.data, fm.protocol.data, fm.dip.data, fm.dport.data)
                        self.render_message_widget(ok=True)
            else:
                self.render_message_widget(messages=fm.messages())

    @tornado.web.authenticated
    def delete(self, rid, nid=None):
        if nid and self.check_state(rid) and self.__check(rid, nid):
            NatDao.delete(nid)
            self.render_message_widget(ok=True)


    def __set_ip_choices(self, rid, form):
        wan, lan = RouterDao.get_network(rid)
        form.dip.choices = ip_choices(lan.net)

    def __check(self, rid, nid):
        if NatDao.get_router(nid) == int(rid):
            return True
        self.render_message_widget(messages=["没有权限"])
        return False


class InputHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid, iid=None):
        if self.check_state(rid):
            form = InputForm("input", "添加入口规则", "/itpkg/%s/input/" % rid, scroll=True)
            if iid:
                if not self.__check(rid, iid):
                    return
                i = InputDao.get(iid)
                form.name.data = i.name
                form.port.data = i.port
                form.protocol.data = i.protocol
                form.id.data = i.id
                form.label = "修改入口规则[%s]" % i.id
            else:
                form.protocol.data = Protocol.TCP
            self.render_form_widget(form=form)

    @tornado.web.authenticated
    def post(self, rid, iid=None):
        if self.check_state(rid):
            fm = InputForm(formdata=self.request.arguments)
            if fm.validate():
                if fm.port.data==22 or InputDao.is_exist(rid, fm.port.data, fm.protocol.data) or NatDao.is_exist(rid, fm.port.data, fm.protocol.data):
                    self.render_message_widget(messages=["规则已存在或端口被占用"])
                else:
                    if fm.id.data:
                        iid = fm.id.data
                        if not self.__check(rid, iid):
                            return
                        InputDao.set(iid, fm.name.data, fm.port.data, fm.protocol.data)
                        self.render_message_widget(ok=True)
                    else:
                        InputDao.add(rid, fm.name.data, fm.port.data, fm.protocol.data)
                        self.render_message_widget(ok=True)
            else:
                self.render_message_widget(messages=fm.messages())

    @tornado.web.authenticated
    def delete(self, rid, iid=None):
        if iid and self.check_state(rid) and self.__check(rid, iid):
            InputDao.delete(iid)
            self.render_message_widget(ok=True)

    def __check(self, rid, iid):
        if InputDao.get_router(iid) == int(rid):
            return True
        self.render_message_widget(messages=["没有权限"])
        return False


class OutputHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid, oid=None):
        if self.check_state(rid):
            form = OutputForm("output", "添加出口规则", "/itpkg/%s/output/" % rid, scroll=True)
            if oid:
                if not self.__check(rid, oid):
                    return
                o = OutputDao.get(oid)
                form.id.data = o.id
                form.name.data = o.name
                form.begin.data = format_time(o.begin)
                form.end.data = format_time(o.end)
                form.keyword.data = o.keyword
                form.weekdays.data = o.weekdays
                form.label = "编辑出口规则[%s]" % o.id
            else:
                form.begin.data = "08:00"
                form.end.data = "20:00"
                form.weekdays.data = "mon,tue,wed,thu,fri"
            self.render_form_widget(form=form)

    @tornado.web.authenticated
    def delete(self, rid, oid=None):
        if oid and self.check_state(rid) and self.__check(rid, oid):
            if OutputDeviceDao.list_device(oid):
                self.render_message_widget(messages=['该规则正在被使用'])
            else:
                OutputDao.delete(oid)
                self.render_message_widget(ok=True)


    @tornado.web.authenticated
    def post(self, rid, oid=None):
        if self.check_state(rid):
            fm = OutputForm(formdata=self.request.arguments)
            if fm.validate():
                if int(fm.begin.data.split(":")[0]) < int(fm.end.data.split(":")[0]):
                    if fm.id.data:
                        oid = fm.id.data
                        OutputDao.set(oid,
                                      fm.name.data,
                                      fm.keyword.data,
                                      fm.begin.data,
                                      fm.end.data,
                                      fm.weekdays.data)
                    else:
                        OutputDao.add(rid,
                                      fm.name.data,
                                      fm.keyword.data,
                                      fm.begin.data,
                                      fm.end.data,
                                      fm.weekdays.data)
                    self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=["起始时间必须小于截止时间,且间隔不小于半个小时"])
            else:
                self.render_message_widget(messages=fm.messages())

    def __check(self, rid, oid):
        if OutputDao.get_router(oid) == int(rid):
            return True
        self.render_message_widget(messages=["没有权限"])
        return False


handlers = [
    (r"/itpkg/([0-9]+)/input/([0-9]*)", InputHandler),
    (r"/itpkg/([0-9]+)/output/([0-9]*)", OutputHandler),
    (r"/itpkg/([0-9]+)/nat/([0-9]*)", NatHandler),
    (r"/itpkg/([0-9]+)/firewall", FirewallHandler),
]