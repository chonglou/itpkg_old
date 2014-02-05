__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.plugins.itpkg.store import LimitDao
from brahma.plugins.itpkg.forms import LimitForm


class LimitHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        manager = self.current_user['id']
        items = LimitDao.all(manager)
        if not items:
            LimitDao.add(manager, "默认规则", 100, 200, 50, 100)
            items = LimitDao.all(manager)
        self.render("itpkg/limit.html", items=items)

    @tornado.web.authenticated
    def put(self):
        lid = self.get_argument("id", None)
        form = LimitForm("limit", "添加限速规则", "/itpkg/limit")
        if lid:
            if self.__check_limit(lid):
                l = LimitDao.get(lid)
                form.id.data = l.id
                form.name.data = l.name
                form.downMax.data = l.downMax
                form.downMin.data = l.downMin
                form.upMax.data = l.upMax
                form.upMin.data = l.upMin
                form.label = "编辑限速规则[%s]" % lid
                self.render_form_widget(form)
                return
        else:
            self.render_form_widget(form)
            return

    @tornado.web.authenticated
    def post(self):
        fm = LimitForm(formdata=self.request.arguments)
        lid = fm.id.data
        messages = []
        if fm.validate():
            if fm.downMax.data >= fm.downMin.data and fm.upMax.data >= fm.upMin.data:
                if lid:
                    if self.__check_limit(lid):
                        LimitDao.set(lid, fm.name.data, fm.upMax.data, fm.downMax.data, fm.upMin.data, fm.downMin.data)
                        self.render_message_widget(ok=True)
                    return
                else:
                    LimitDao.add(self.current_user['id'], fm.name.data, fm.upMax.data, fm.downMax.data, fm.upMin.data,
                                 fm.downMin.data)
                    self.render_message_widget(ok=True)
                    return
            else:
                messages.append("最大上传/下载速率应该不小于最小上传/下载速率")

        messages.extend(fm.messages())
        self.render_message_widget(messages=messages)


    def __check_limit(self, lid):
        l = LimitDao.get(lid)
        manager = self.current_user['id']
        if l.manager == manager:
            return True
        self.render_message_widget(messages=['没有权限'])
        return False


handlers = [
    (r"/itpkg/limit", LimitHandler),
]