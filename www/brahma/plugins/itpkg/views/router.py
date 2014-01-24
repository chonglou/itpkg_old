__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.web import Message
from brahma.plugins.itpkg.views import BaseHandler
from brahma.plugins.itpkg.forms import RouterForm
from brahma.plugins.itpkg.store import RouterDao


class RouterHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, rid):
        if self.check_router(rid):
            self.render_ctlbar_widget(act="/itpkg/%s" % rid,
                                      items=[
                                          ("user", "用户"),
                                          ("group", "用户组"),
                                          ("dhcp", "DHCP服务"),
                                          ("dns", "DNS服务"),
                                          ("firewall", "防火墙"),
                                          ("limit", "限速规则"),
                                          ("wan", "WAN配置"),
                                          ("lan", "LAN配置"),
                                          ("status", "当前状态"),
                                      ])


class ListHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render("itpkg/router.html", routers=RouterDao.all(self.current_user['id']))

    @tornado.web.authenticated
    def post(self):
        fm = RouterForm(formdata=self.request.arguments)

        if fm.validate():
            rid = fm.rid.data
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
        form = RouterForm("router", "添加路由器", "/itpkg/router")
        if rid:
            if self.check_router(rid):
                form.label = "编辑路由器[%s]" % rid
                r = RouterDao.get(rid)
                form.rid.data = r.id
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
]