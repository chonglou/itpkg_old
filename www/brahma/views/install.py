__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.web

from brahma.forms.site import InstallForm
from brahma.models import Setting, User, Log, Permission
from brahma.web import Message


class InstallHandler(tornado.web.RequestHandler):
    def __check(self):
        from brahma.cache import get_site_info

        if get_site_info("version"):
            self.redirect("/main")
            return False
        return True

    def __render_page(self, page, title, **kwargs):
        self.render(page, title=title, navItems=list(), tagLinks=list(), index=None, is_login=False, **kwargs)

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
            self.__render_page("install.html", "初始化安装", jsessionid=uuid.uuid4().hex, form=InstallForm("install", "初始化安装", "/install", captcha=True))

    def post(self):
        if self.__check():
            form = InstallForm(formdata=self.request.arguments)

            messages = []
            if form.validate():
                if self.get_argument("captcha") == self.get_secure_cookie("captcha").decode("utf-8"):
                    from brahma.env import db_call

                    @db_call
                    def install(session):
                        import pickle
                        from brahma.env import encrypt

                        session.add(Setting("site.init", pickle.dumps(datetime.datetime.now())))
                        session.add(Setting("site.domain", pickle.dumps(form.siteDomain.data)))
                        session.add(Setting("site.title", pickle.dumps(form.siteTitle.data)))
                        session.add(Setting("site.keywords", pickle.dumps(form.siteKeywords.data)))
                        session.add(Setting("site.description", pickle.dumps(form.siteDescription.data)))

                        session.add(Setting("site.smtp",
                                            encrypt.encode({
                                                'host': form.smtpHost.data,
                                                'port': form.smtpPort.data,
                                                'ssl': form.smtpSsl.data,
                                                'username': form.smtpUsername.data,
                                                'password': form.smtpPassword.data,
                                                'bcc': form.smtpBcc.data,
                                            }))
                        )

                        email = form.managerEmail.data
                        user = User("email", email=email, username="超级管理员", password=form.managerPassword.data)
                        user.state = "ENABLE"
                        session.add(user)
                        user = session.query(User).filter(User.email == email).one()
                        session.add(Setting("site.manager", encrypt.encode(user.id)))

                        session.add(Setting("site.link.valid", pickle.dumps(24)))

                        session.add(Setting("site.version", pickle.dumps("v20140112")))
                        session.add(Log(user=user.id, flag="INFO", message="初始化系统"))

                        session.add(Permission("user://%d" % user.id, "MANAGER", "SITE", datetime.datetime.now(),
                                               datetime.datetime.max))

                    install()

                    from brahma.cache import get_site_info

                    for s in ["title", "description", "keywords", "version"]:
                        get_site_info(s, True)
                    self.__render_message(Message(ok=True, messages=["请刷新页面"], goto="/main"))
                    return
                messages.append("验证码不对")
            else:
                messages.extend(form.messages())

            self.__render_message(Message(messages=messages))


handlers = [
    (r"/install", InstallHandler),
]