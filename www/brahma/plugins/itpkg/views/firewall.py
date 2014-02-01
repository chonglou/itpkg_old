__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao, InputDao, OutputDao, NatDao, OutputDeviceDao, DeviceDao
from brahma.plugins.itpkg.forms import InputForm, OutputForm,NatForm, ip_choices


class FirewallHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            self.render("itpkg/firewall/index.html", rid=rid,
                        ins=InputDao.all(rid),
                        outs = InputDao.all(rid))

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")
            from brahma.plugins.itpkg.rpc import create

            r = RouterDao.get(rid)
            rpc = create(rid)
            import json

            lan = json.loads(r.lan)
            if act == "input":
                form = InputForm("input", "添加入口规则", "/itpkg/%s/firewall" % rid)
                form.act.data = "input"
                form.protocol.data = "tcp"
                self.render("itpkg/firewall/input.html", form=form, items=InputDao.all(rid))
            elif act == "output":
                form = OutputForm("output", "添加出口规则", "/itpkg/%s/firewall" % rid)
                form.act.data = "output"
                form.begin.data = "08:00"
                form.end.data = "18:00"
                form.weekdays.data = self.__output_weekdays()
                self.render("itpkg/firewall/output.html", form=form, items=OutputDao.all(rid))
            elif act == "nat":
                form = NatForm("nat", "添加映射规则", "/itpkg/%s/firewall" % rid)
                form.act.data = "nat"
                form.protocol.data = "tcp"
                form.dip.choices = ip_choices(lan['net'])
                self.render("itpkg/firewall/nat.html", form=form)
            elif act == "apply":
                #test
                ins = []
                for i in InputDao.all(rid):
                    ins.append(("tcp" if i.tcp else "udp", i.port))

                outs = []
                for o in OutputDao.all(rid):
                    #todo 当前只支持关键字
                    ms = []
                    for od in OutputDeviceDao.all(o.id):
                        ms.append(DeviceDao.get(od.device).mac)

                    outs.append(
                        (o.keyword, o.start.strftime("%H:%M"), o.end.strftime("%H:%M"), self.__output_weekdays(o), ms))

                nats = []
                for n in NatDao.all(rid):
                    nats.append((n.sport, "tcp" if n.tcp else "udp", n.dip, n.dport))

                macs = []
                for d in DeviceDao.all(rid):
                    if d.state == "ENABLE":
                        macs.append(d.mac)

                ok, result = rpc.apply_firewall(net=lan['net'], ins=ins, outs=outs, nats=nats, macs=macs)
                if ok:
                    self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=result)
            elif act == "clear":
                #test
                ok, result = rpc.clear_firewall(lan['net'])
                self.render_message_widget(ok=ok, messages=result)
            elif act == "status":
                #test
                ok, result = rpc.status_firewall()
                self.render_message_widget(ok=ok, messages=result)
            else:
                self.render_message_widget(messages=["未知操作"])

    @tornado.web.authenticated
    def post(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")
            if act == "input":
                fm = InputForm(formdata=self.request.arguments)
                if fm.validate():
                    if InputDao.is_exist(rid, fm.port.data,fm.protocol.data == "tcp"):
                        self.render_message_widget(messages=["规则已存在"])
                    else:
                        InputDao.add(rid, fm.port.data,fm.protocol.data == "tcp")
                        self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=fm.messages())
            elif act == "output":
                fm = OutputForm(formdata=self.request.arguments)
                if fm.validate():
                    if OutputDao.is_exist(rid, fm.keyword.data):
                        self.render_message_widget(messages=["规则已存在"])
                    else:
                        OutputDao.add(rid,
                                      fm.begin.data,
                                      fm.end.data,
                                      "mon"in fm.weekdays.data)
                        self.render_message_widget(ok=True)

            else:
                self.render_message_widget(messages=["错误请求"])

    def __output_weekdays(self, output=None):
        if not output:
            return "mon,tue,wed,thu,fri"
        ws = []
        if output.mon:
            ws.append("mon")
        if output.tue:
            ws.append("tue")
        if output.wed:
            ws.append("wed")
        if output.thu:
            ws.append("thu")
        if output.fri:
            ws.append("fri")
        if output.sat:
            ws.append("sat")
        if output.sun:
            ws.append("sun")
        return ",".join(ws)


handlers = [
    (r"/itpkg/([0-9]+)/firewall", FirewallHandler),
]