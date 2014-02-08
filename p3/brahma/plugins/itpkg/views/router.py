__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.forms import InfoForm
from brahma.plugins.itpkg.store import RouterDao


class RouterHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_manager(rid):
            self.render_ctlbar_widget(act="/itpkg/%s" % rid,
                                      items=[
                                          ("network", "网络配置"),
                                          ("device", "设备管理"),
                                          ("dhcp", "DHCP"),
                                          ("dns", "DNS"),
                                          ("firewall", "防火墙"),
                                          ("status", "当前状态"),
                                      ])


class ListHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render("itpkg/router.html", routers=RouterDao.list_by_manager(self.current_user['id']))

    @tornado.web.authenticated
    def post(self):
        fm = InfoForm(formdata=self.request.arguments)

        if fm.validate():
            rid = fm.iid.data
            if rid:
                if self.check_manager(rid):
                    RouterDao.set_info(rid, fm.name.data, fm.details.data)
                    self.render_message_widget(ok=True)
            else:
                RouterDao.add(self.current_user['id'], fm.name.data, fm.details.data)
                self.render_message_widget(ok=True)
        else:
            messages = []
            messages.extend(fm.messages())
            self.render_message_widget(messages=messages)

    @tornado.web.authenticated
    def put(self):
        rid = self.get_argument("id", None)
        form = InfoForm("router", "添加路由器", "/itpkg/router")
        if rid:
            if self.check_manager(rid):
                form.label = "编辑路由器[%s]" % rid
                r = RouterDao.get_info(rid)
                form.iid.data = rid
                form.name.data = r.name
                form.details.data = r.details
                self.render_form_widget(form=form)
        else:
            self.render_form_widget(form=form)


handlers = [
    (r"/itpkg/router", ListHandler),
    (r"/itpkg/router/([0-9]+)", RouterHandler),
]