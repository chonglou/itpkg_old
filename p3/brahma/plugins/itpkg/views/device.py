__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import DeviceDao, RouterDao, UserDao, LimitDao
from brahma.plugins.itpkg.rpc import create as create_rpc
from brahma.plugins.itpkg.forms import DeviceForm


class DeviceHandler(BaseHandler):
    def __check_device(self, rid, did):
        d = DeviceDao.get(did)
        if type(rid) == str:
            rid = int(rid)

        if d.router == rid:
            return True
        self.render_message_widget(messages=['没有权限'])
        return False

    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            import json

            r = RouterDao.get(rid)
            lan = json.loads(r.lan)
            devices = DeviceDao.all(rid)
            users = {}
            limits = {}
            if devices:
                for d in devices:
                    users[d.id] = UserDao.get(d.user).name if d.user else None
                    limits[d.id] = LimitDao.get(d.limit).name if d.limit else None
            self.render("itpkg/device/list.html", rid=rid, devices=devices, users=users, limits=limits, net=lan['net'])

    @tornado.web.authenticated
    def post(self, rid):
        manager = self.current_user['id']
        act = self.get_argument("act")
        if self.check_state(rid):
            if act == "edit":
                fm = DeviceForm(formdata=self.request.arguments)
                fm.user.choices = [(u.id, u.name) for u in UserDao.all(manager)]
                fm.limit.choices = [(l.id, l.name) for l in LimitDao.all(manager)]
                if self.__check_device(rid, fm.id.data):
                    if fm.validate():
                        DeviceDao.set(fm.id.data, fm.user.data, fm.limit.data,
                                      "ENABLE" if fm.enable.data else "DISABLE", fm.details.data)
                        self.render_message_widget(ok=True)
                    else:
                        self.render_message_widget(messages=fm.messages())
            else:
                self.render_message_widget(messages=['未知操作'])

    @tornado.web.authenticated
    def put(self, rid):
        manager = self.current_user['id']
        act = self.get_argument("act")
        if self.check_state(rid):
            import json

            r = RouterDao.get(rid)
            net = json.loads(r.lan)['net']
            if act == "edit":
                did = self.get_argument("id")
                if self.__check_device(rid, did):
                    users = UserDao.all(manager)
                    limits = LimitDao.all(manager)
                    if users and limits:
                        device = DeviceDao.get(did)
                        form = DeviceForm("device", "设备[%s]详细信息" % device.id, "/itpkg/%s/device" % rid)
                        form.act.data = "edit"
                        form.id.data = device.id
                        form.user.choices = [(u.id, u.name) for u in users]
                        form.limit.choices = [(l.id, l.name) for l in limits]
                        form.enable.data = device.state == "ENABLE"
                        form.user.data = device.user
                        form.limit.data = device.limit
                        form.details.data = device.details
                        self.render_form_widget(form)
                    else:
                        self.render_message_widget(messages=["请添加默认用户和限速规则列表"])
            elif act == "view":
                did = self.get_argument("id")
                if self.__check_device(rid, did):
                    self.render("itpkg/device/view.html", net=net, device=DeviceDao.get(did))
            elif act == "scan":
                #test
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
            elif act == "debug":
                items = list()
                for i in range(1, 20):
                    items.append(("mac-%s" % i, i))
                DeviceDao.fill(rid, items)
                self.render_message_widget(ok=True)
            else:
                self.render_message_widget(messages=['错误请求'])


handlers = [
    (r"/itpkg/([0-9]+)/device", DeviceHandler),
]