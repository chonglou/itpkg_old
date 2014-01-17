__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.forms.personal import LoginForm, RegisterForm, ActiveForm, ResetPwdForm
from brahma.store.site import UserDao


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
        if self.check_non_login():
            self.render(
                "widgets/form.html",
                form=LoginForm(fid="login", label="欢迎登录", action="/personal/login", captcha=True)
            )

    def post(self):
        if self.check_non_login():
            fm = LoginForm(formdata=self.request.arguments)
            messages = []
            if self.check_captcha():
                if fm.validate():
                    user = UserDao.auth("email", email=fm.email.data, password=fm.password.data)
                    if user:
                        self.set_current_user({
                            "id": user.id,
                            #"logo": user.logo, TODO
                            "username": user.username,
                        })
                        self.render_message_widget(ok=True, goto="/main")
                        return
                    else:
                        messages.append("登录邮箱和密码不匹配")
                else:
                    messages.extend(fm.messages())
            else:
                messages.append("验证码不对")
            self.render_message_widget(ok=False, messages=messages)


class LogoutHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.clear_cookie("user")
        self.goto_main_page()


handlers = [
    (r"/personal/active", ActiveHandler),
    (r"/personal/resetPwd", ResetPwdHandler),
    (r"/personal/login", LoginHandler),
    (r"/personal/register", RegisterHandler),
    (r"/personal/logout", LogoutHandler),
]