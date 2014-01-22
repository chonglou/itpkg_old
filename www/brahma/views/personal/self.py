__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.web

from brahma.views import BaseHandler
from brahma.web import Message
from brahma.forms.personal import ContactForm, SetPwdForm
from brahma.store.site import LogDao, UserDao


class InfoHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, act):
        if act == "logs":
            self.render_list_widget("日志列表", items=["[%s] %s： %s" % (l.flag, l.created.isoformat(), l.message) for l in
                                                   LogDao.list_log(datetime.datetime.min, datetime.datetime.max,
                                                                   user=self.current_user['id'], limit=20)])
        elif act == "setPwd":
            form = SetPwdForm("setPwd", "修改密码", "/personal/info/setPwd")
            self.render_form_widget(form=form)
        elif act == "contact":
            form = ContactForm("contact", "联系信息", "/personal/info/contact")
            user = UserDao.get_by_id(self.current_user['id'])
            if user.contact:
                import json

                form.from_dict(json.loads(user.contact))
            form.username.data = user.username
            form.logo.data = user.logo
            self.render_form_widget(form=form)
        else:
            self.render_message_widget(Message(messages=["未知操作"]))

    @tornado.web.authenticated
    def post(self, act):
        messages = []
        if act == "setPwd":

            fm = SetPwdForm(formdata=self.request.arguments)
            if fm.validate():
                uid = self.current_user['id']
                u = UserDao.get_by_id(uid)
                if u.check(fm.oldPassword.data):
                    UserDao.set_password(uid, fm.password.data)
                    LogDao.add_log("修改密码", user=uid)
                    self.render_message_widget(Message(ok=True))
                    return
                else:
                    messages.append("旧密码输入有误")
            else:
                messages.extend(fm.messages())
        elif act == "contact":
            fm = ContactForm(formdata=self.request.arguments)
            if fm.validate():
                UserDao.set_info(self.current_user['id'], fm.username.data, fm.logo.data,
                                 fm.to_dict(
                                     ["qq", "email", "website", "wechat", "weibo", "address", "fax", "tel", "details"]))
                self.render_message_widget(Message(ok=True))
                return
            else:
                messages.extend(fm.messages())
        else:
            messages.append("未知操作")

        self.render_message_widget(Message(messages=messages))


class SelfHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        return self.render_ctlbar_widget(act="/personal/info",
                                         items=[
                                             ("contact", "联系信息"),
                                             ("setPwd", "修改密码"),
                                             ("logs", "日志列表"),
                                         ])


handlers = [
    (r"/personal/self", SelfHandler),
    (r"/personal/info/(.*)", InfoHandler),
]
