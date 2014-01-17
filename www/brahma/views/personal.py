__author__ = 'zhengjitang@gmail.com'

from brahma.views import BaseHandler
from brahma.forms.personal import LoginForm, RegisterForm, ActiveForm, ResetPwdForm


class ActiveHandler(BaseHandler):
    def get(self):
        self.render("widgets/form.html",
                    form=ActiveForm(fid="active", label="账户激活", action="/personal/active", captcha=True)
        )

    def post(self):
        self.goto_main_page()


class ResetPwdHandler(BaseHandler):
    def get(self):
        self.render("widgets/form.html",
                    form=ResetPwdForm(fid="resetPwd", label="重置密码", action="/personal/resetPwd", captcha=True)
        )

    def post(self):
        self.goto_main_page()


class RegisterHandler(BaseHandler):
    def get(self):
        self.render("widgets/form.html",
                    form=RegisterForm(fid="register", label="账户注册", action="/personal/register", captcha=True)
        )

    def post(self):
        self.goto_main_page()


class LoginHandler(BaseHandler):
    def get(self):
        self.render("widgets/form.html",
                    form=LoginForm(fid="login", label="欢迎登录", action="/personal/login", captcha=True)
        )

    def post(self):
        self.goto_main_page()


class LogoutHandler(BaseHandler):
    def get(self):
        self.goto_main_page()


handlers = [
    (r"/personal/active", ActiveHandler),
    (r"/personal/resetPwd", ResetPwdHandler),
    (r"/personal/login", LoginHandler),
    (r"/personal/register", RegisterHandler),
    (r"/personal/logout", LogoutHandler),
]