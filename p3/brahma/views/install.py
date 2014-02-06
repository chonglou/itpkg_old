__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.web

from brahma.forms.site import InstallForm
from brahma.store import Setting, User, Log, Permission
from brahma.web import Message


class InstallHandler(tornado.web.RequestHandler):
    def __check(self):
        from brahma.cache import get_site_info

        if get_site_info("version"):
            #如果需要重新安装，请清理浏览器cache
            self.redirect("/main", permanent=True)
            return False
        return True

    def __render_page(self, page, title, **kwargs):
        self.render(page, title=title, index=None, is_login=False, **kwargs)

    def __render_message(self, msg):
        from brahma.widgets import Message

        m = Message(self)
        self.write(m.render(msg=msg))
        self.write('<script type="text/javascript">')
        self.write(m.embedded_javascript())
        self.write('</script>')

    def get(self):
        if self.__check():
            import uuid

            self.__render_page("install.html", "初始化安装", jsessionid=uuid.uuid4().hex,
                               form=InstallForm("install", "初始化安装", "/install", captcha=True, body=False))

    def post(self):
        if self.__check():
            form = InstallForm(formdata=self.request.arguments)

            messages = []
            if form.validate():
                if self.get_argument("captcha") == self.get_secure_cookie("captcha").decode("utf-8"):
                    from brahma.env import transaction

                    @transaction(False)
                    def install(cursor=None):
                        from brahma.models import Item, State, LogFlag, Operation

                        for k, v in [
                            ("site.init", datetime.datetime.now()),
                            ("site.domain", form.siteDomain.data),
                            ("site.title", form.siteTitle.data),
                            ("site.keywords", form.siteKeywords.data),
                            ("site.description", form.siteDescription.data),
                        ]:
                            Setting._set(k, v, False, cursor)

                        Setting._set("site.smtp", Item(
                            host=form.smtpHost.data,
                            port=form.smtpPort.data,
                            ssl=form.smtpSsl.data,
                            username=form.smtpUsername.data,
                            password=form.smtpPassword.data,
                            bcc=form.smtpBcc.data,
                        ).__dict__,
                                     True, cursor)

                        email = form.managerEmail.data
                        manager = User._add_email(email=email, username="超级管理员", password=form.managerPassword.data,
                                                  cursor=cursor)
                        User._set_state(manager, State.ENABLE, cursor=cursor)
                        Permission._bind("user://%d" % manager, Operation.MANAGER, "SITE", datetime.datetime.now(),
                                         datetime.datetime.max, True, cursor)

                        Setting._set("site.manager", manager, True, cursor)
                        Setting._set("site.link.valid", 24, False, cursor)
                        Setting._set("site.version", "v20140205", False, cursor)

                        Log._add(user=manager, flag=LogFlag.INFO, message="初始化系统", cursor=cursor)

                    install()

                    from brahma.cache import get_site_info

                    for s in ["title", "description", "keywords", "version"]:
                        get_site_info(s, True)
                    self.__render_message(Message(ok=True, messages=["将跳转至主页"], goto="/main"))
                    return
                messages.append("验证码不对")
            else:
                messages.extend(form.messages())

            self.__render_message(Message(messages=messages))


handlers = [
    (r"/install", InstallHandler),
]