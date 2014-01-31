__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import RouterDao,InputDao,OutputDao,NatDao,OutputDeviceDao, DeviceDao


class FirewallHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            self.render("itpkg/firewall.html", rid=rid)

    @tornado.web.authenticated
    def put(self, rid):
        if self.check_state(rid):
            act = self.get_argument("act")
            from brahma.plugins.itpkg.rpc import create
            r = RouterDao.get(rid)
            rpc = create(rid)
            import json
            lan = json.loads(r.lan)
            wan = json.loads(r.wan)
            if act == "apply":
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
                    ws = []
                    if o.mon:
                        ws.append("mon")
                    if o.tue:
                        ws.append("tue")
                    if o.wed:
                        ws.append("wed")
                    if o.thu:
                        ws.append("thu")
                    if o.fri:
                        ws.append("fri")
                    if o.sat:
                        ws.append("sat")
                    if o.sun:
                        ws.append("sun")
                    outs.append((o.keyword, o.start.strftime("%H:%M"), o.end.strftime("%H:%M"),",".join(ws),ms))

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
        #todo
        pass


handlers = [
    (r"/itpkg/([0-9]+)/firewall", FirewallHandler),
]