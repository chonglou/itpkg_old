__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import DeviceDao,RouterDao,UserDao
from brahma.plugins.itpkg.rpc import create as create_rpc
from brahma.plugins.itpkg.forms import DeviceInfoForm,DeviceBindForm



class DeviceHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            import json
            r = RouterDao.get(rid)
            lan = json.loads(r.lan)
            self.render("itpkg/device/list.html", rid=rid, devices=DeviceDao.all(rid), net=lan['net'])

    @tornado.web.authenticated
    def post(self, rid):
        manager = self.current_user['id']
        act = self.get_argument("act")
        if self.check_state(rid):
            if act == "edit":
                fm = DeviceInfoForm(formdata=self.request.arguments)
                fm.user.choices = [(str(u.id), u.name) for u in UserDao.all(manager)]
                if fm.validate():
                    DeviceDao.set_info(fm.id.data, fm.user.data, fm.details.data)
                    self.render_message_widget(ok=True)
                else:
                    self.render_message_widget(messages=fm.messages())

    @tornado.web.authenticated
    def put(self, rid):
        manager = self.current_user['id']
        act = self.get_argument("act")
        if self.check_state(rid):
            import json
            r = RouterDao.get(rid)
            net = json.loads(r.lan)['net']
            if act == "edit":
                device = DeviceDao.get(self.get_argument("id"))
                form = DeviceInfoForm("device", "设备[%s]详细信息"%device.id, "/itpkg/%s/device" % rid)
                form.act.data = "edit"
                form.id.data = device.id
                form.user.choices = [(u.id, u.name) for u in UserDao.all(manager)]
                form.user.data = device.user
                form.details.data = device.details
                self.render_form_widget(form)
            elif act == "view":
                self.render("itpkg/device/view.html", net=net, device=DeviceDao.get(self.get_argument("id")))
            elif act == "scan":
                #todo test
                rpc = create_rpc(rid)
                ok, result = rpc.scan()
                if ok:
                    i, u = DeviceDao.fill(rid, result)
                    self.render_message_widget(ok=True,
                                               messages=[
                                                   "新增了%s条记录" % i,
                                                   "更新了%s条记录" % u,
                                               ]
                    )
                else:
                    self.render_message_widget(messages=result)
            elif act == "fix":
                form = DeviceBindForm("bind", "固定IP绑定", "/itpkg/%s/device"%rid)
                form.act.data = "fix"
                form.ip.choices = [(i, "%s.%s"%(net, i)) for i in range(2, 254)]
                form.mac.choices = [(d.id, d.mac) for d in DeviceDao.all(rid)]
                self.render_form_widget(form)
            #elif act == "test":
            #    items = list()
            #    for i in range(1, 20):
            #        items.append(("mac-%s"%i,i))
            #    DeviceDao.fill(rid, items)
            #    self.render_message_widget(ok=True)


handlers = [
    (r"/itpkg/([0-9]+)/device", DeviceHandler),
]