__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.web

from brahma.store.site import SiteDao
from brahma.forms.site import InstallForm
from brahma.models import Setting, User
from brahma.env import cache


def get_site_info():
    return {
        "title": "", "keywords": "", "description": "", "domain": "",
        "sidebars": [], "tags": [], "topLinks": [],
        "adLeft": "left", "adMain": "main",
    }


class InstallHandler(tornado.web.RequestHandler):
    def __check(self):
        v = SiteDao.get("site.version")
        if v:
            cache.set("site/version", val=v)
            self.render("message.html",
                        title="提示信息", site=get_site_info(), is_login=False, index=None,
                        ok=True, confirm=None, messages=["已经安装"], goto="/main")
            return False
        return True

    def get(self):
        if self.__check():
            self.render("install.html", title="初始化安装", site=get_site_info(), index=None, is_login=False)

    def put(self):
        if self.__check():
            form = InstallForm("install", "初始化安装", "/install", captcha=True)
            self.render("widgets/form.html", form=form)

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
                        session.add(User("email", email=email, username="超级管理员", password=form.managerPassword.data))
                        user = session.query(User).filter(User.email == email).one()
                        session.add(Setting("site.manager", encrypt.encode(user.id)))

                        session.add(Setting("site.version", pickle.dumps("v20140112")))

                    install()

                    self.render("widgets/message.html", ok=True, confirm=False, messages=["请刷新页面"], goto="/main")
                    return
                messages.append("验证码不对")
            else:
                messages.extend(form.messages())

            self.render("widgets/message.html", ok=False, confirm=False, messages=messages, goto=None)


handlers = [
    (r"/install", InstallHandler),
]