__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.store import DeviceDao, RouterDao, UserDao, LimitDao
from brahma.plugins.itpkg.rpc import create as create_rpc
from brahma.plugins.itpkg.forms import DeviceForm
from brahma.models import State


class DeviceHandler(BaseHandler):
    def __check_device(self, rid, did):
        if DeviceDao.get_router(did) == int(rid):
            return True
        self.render_message_widget(messages=['没有权限'])
        return False

    @tornado.web.authenticated
    def get(self, rid):
        if self.check_state(rid):
            wan, lan = RouterDao.get_network(rid)
            devices = DeviceDao.list_by_router(rid)
            users = {}
            limits = {}
            if devices:
                for d in devices:
                    users[d.id] = UserDao.get_name(d.user) if d.user else None
                    limits[d.id] = LimitDao.get_name(d.limit) if d.limit else None
            self.render("itpkg/device/list.html", rid=rid, devices=devices, users=users, limits=limits, net=lan['net'])

    @tornado.web.authenticated
    def post(self, rid):
        manager = self.current_user['id']
        act = self.get_argument("act")
        if self.check_state(rid):
            if act == "edit":
                fm = DeviceForm(formdata=self.request.arguments)
                fm.user.choices = UserDao.choices_by_manager(manager)
                fm.limit.choices = LimitDao.choices_by_manager(manager)
                if self.__check_device(rid, fm.id.data):
                    if fm.validate():
                        DeviceDao.set(fm.id.data, fm.user.data, fm.limit.data,
                                      State.ENABLE if fm.enable.data else State.DISABLE, fm.details.data)
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

            wan, lan = RouterDao.get_network(rid)
            if act == "edit":
                did = self.get_argument("id")
                if self.__check_device(rid, did):
                    users = UserDao.choices_by_manager(manager)
                    limits = UserDao.choices_by_manager(manager)
                    if users and limits:
                        device = DeviceDao.get(did)
                        form = DeviceForm("device", "设备[%s]详细信息" % device.id, "/itpkg/%s/device" % rid)
                        form.act.data = "edit"
                        form.id.data = device.id
                        form.user.choices = users
                        form.limit.choices = limits
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
                    self.render("itpkg/device/view.html", net=lan.net, device=DeviceDao.get(did))
            elif act == "scan":
                #todo
                rpc = create_rpc(rid)
                ok, result = rpc.scan()
                if ok:
                    i, u = DeviceDao.add_all(rid, result)
                    self.render_message_widget(ok=True,
                                               messages=[
                                                   "新增了%s条记录" % i,
                                                   "更新了%s条记录" % u,
                                               ]
                    )
                else:
                    self.render_message_widget(messages=result)
            else:
                self.render_message_widget(messages=['错误请求'])


handlers = [
    (r"/itpkg/([0-9]+)/device", DeviceHandler),
]