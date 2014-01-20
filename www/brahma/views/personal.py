__author__ = 'zhengjitang@gmail.com'

import logging
import tornado.web
from brahma.views import BaseHandler
from brahma.forms.personal import LoginForm, RegisterForm, ActiveForm, ResetPwdForm
from brahma.store.site import UserDao, SettingDao
from brahma.web import NavBar, Message


class SelfHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        nv_self = NavBar("控制面板")
        if self.is_admin():
            nv_self.add("/admin/site", "站点设置")
        nv_self.add("/personal/info", "个人信息")
        nv_self.add("/personal/info", "日志列表")

        return self.render_page("personal/self.html", index="/personal/self", title="用户中心", navBars=[nv_self])


class ValidHandler(BaseHandler):
    def get(self):
        ok = False
        messages = []
        if self.check_non_login():
            import datetime
            from brahma.env import encrypt
            from brahma.store.site import UserDao

            try:
                flag, dt, args = encrypt.decode(self.get_argument("code").encode("utf-8"))
                if dt > datetime.datetime.now():
                    if flag == "active":
                        user = UserDao.get_by_email(args)
                        if user and user.state == "SUBMIT":
                            UserDao.set_state(user.id, "ENABLE")
                            ok = True
                            domain = SettingDao.get("site.domain")
                            title = SettingDao.get("site.title")
                            from brahma.jobs import TaskSender

                            TaskSender.send_email(
                                user.email,
                                "账户激活成功--来自[%s](%s)的邮件" % (title, domain),
                                "亲爱的[%s]：<br>您好<br>您刚刚激活了账户，祝您一切顺利。谢谢" % user.username)
                            messages.append("账户激活成功")
                        else:
                            messages.append("账户状态不对")
                    elif flag == "resetPwd":
                        uid, password = args
                        user = UserDao.get_by_id(uid)
                        if user and user.state == "ENABLE":
                            UserDao.set_password(uid, password)
                            ok = True
                            domain = SettingDao.get("site.domain")
                            title = SettingDao.get("site.title")
                            from brahma.jobs import TaskSender

                            TaskSender.send_email(
                                user.email,
                                "重置密码成功--来自[%s](%s)的邮件" % (title, domain),
                                "亲爱的[%s]：<br>您好<br>您刚刚重置了密码，祝您一切顺利。谢谢" % user.username)
                            messages.append("密码重置成功")
                        else:
                            messages.append("账户状态不对")
                    else:
                        messages.append("未知的操作")
                else:
                    messages.append("链接已过期，请重新申请激活")
            except:
                logging.exception("链接验证出错")
                messages.append("链接无效")
        self.render_message(title="提示信息", ok=ok, messages=messages, goto="/main")


class ActiveHandler(BaseHandler):
    def get(self):
        if self.check_non_login():
            self.render_form_widget(
                form=ActiveForm(fid="active", label="账户激活", action="/personal/active", captcha=True))

    def post(self):
        if self.check_non_login():
            fm = ActiveForm(formdata=self.request.arguments)
            messages = []
            if self.check_captcha():
                if fm.validate():
                    if fm.agree.data:
                        email = fm.email.data
                        user = UserDao.get_by_email(email)
                        if user:
                            if user.state == "SUBMIT":
                                _send_active_email(email, user.username)
                                self.render_message_widget(Message(ok=True, messages=["账户添加成功", "现在你需要进入邮箱点击链接激活邮件"],
                                                                   goto="/main"))
                                return
                            else:
                                messages.append("用户[%s]状态不对" % email)
                        else:
                            messages.append("用户[%s]不存在" % email)
                    else:
                        messages.append("你需要同意协议才能继续")
                else:
                    messages.append(fm.messages())
            else:
                messages.append("验证码不对")
            self.render_message_widget(Message(messages=messages))


class ResetPwdHandler(BaseHandler):
    def get(self):
        if self.check_non_login():
            self.render_form_widget(
                form=ResetPwdForm(fid="resetPwd", label="重置密码", action="/personal/resetPwd", captcha=True))

    def post(self):
        if self.check_non_login():
            fm = ResetPwdForm(formdata=self.request.arguments)
            messages = []
            if self.check_captcha():
                if fm.validate():
                    email = fm.email.data
                    user = UserDao.get_by_email(email)
                    if user and user.state == "ENABLE":
                        from brahma.env import encrypt

                        _send_valid_email("resetPwd", email, user.username, "重置密码", (user.id, fm.password.data))
                        self.render_message_widget(
                            Message(ok=True, goto="/main", messages=["重置密码申请提交成功", "请进入邮箱点击链接生效"]))
                        return
                    else:
                        messages.append("用户[%s]状态不对" % email)
                else:
                    messages.extend(fm.messages())
            else:
                messages.append("验证码不对")
            self.render_message_widget(Message(messages=messages))


class RegisterHandler(BaseHandler):
    def get(self):
        if self.check_non_login():
            self.render_form_widget(
                form=RegisterForm(fid="register", label="账户注册", action="/personal/register", captcha=True))

    def post(self):
        if self.check_non_login():
            fm = RegisterForm(formdata=self.request.arguments)
            messages = []
            if self.check_captcha():
                if fm.validate():
                    if fm.agree.data:
                        email = fm.email.data
                        username = fm.username.data
                        user = UserDao.get_by_email(email)
                        if not user:
                            UserDao.add_user("email", username=username, email=email, password=fm.password.data)
                            _send_active_email(email, username)
                            self.render_message_widget(
                                Message(ok=True, messages=["账户添加成功", "现在你需要进入邮箱点击链接激活邮件"], goto="/main"))
                            return
                        else:
                            messages.append("邮箱[%s]已存在" % email)
                    else:
                        messages.append("你需要同意协议才能继续")
                else:
                    messages.append(fm.messages())
            else:
                messages.append("验证码不对")
            self.render_message_widget(Message(messages=messages))


def _send_valid_email(flag, email, username, action, args):
    domain = SettingDao.get("site.domain")
    title = SettingDao.get("site.title")
    linkValid = SettingDao.get("site.link.valid")

    from brahma.jobs import TaskSender
    from brahma.env import encrypt
    import datetime

    url = "http://%s/personal/valid?code=%s" % (domain, encrypt.encode(
        (flag,
         datetime.datetime.now() + datetime.timedelta(hours=linkValid),
         args)).decode("utf-8")
    )
    TaskSender.send_email(
        email,
        "%s -- 来自[%s]（%s）的邮件" % (action, title, domain),
        """亲爱的用户[%s]:<br/>
        您好<br/>
        欢迎在<a href='http://%s'>%s</a>上%s，请点击如下链接继续（链接%d小时内有效）：
        <a href='%s'>%s</a><br><br>谢谢""" % (username, domain, title, action, linkValid, url, url)
    )


def _send_active_email(email, username):
    _send_valid_email("active", email, username, "账户激活", email)


class LoginHandler(BaseHandler):
    def get(self):
        if self.check_non_login():
            self.render_form_widget(form=LoginForm(fid="login", label="欢迎登录", action="/personal/login", captcha=True))

    def post(self):
        if self.check_non_login():
            fm = LoginForm(formdata=self.request.arguments)
            messages = []
            if self.check_captcha():
                if fm.validate():
                    user = UserDao.auth("email", email=fm.email.data, password=fm.password.data)
                    if user:
                        if user.state == "ENABLE":
                            from brahma.store.rbac import RbacDao

                            self.set_current_user({
                                "id": user.id,
                                "logo": user.logo,
                                "username": user.username,
                                "admin": RbacDao.auth4admin(user.id),
                            })
                            self.render_message_widget(Message(ok=True, goto="/main"))
                            return
                        else:
                            messages.append("账户未激活，或被禁用。")
                    else:
                        messages.append("登录邮箱和密码不匹配")
                else:
                    messages.extend(fm.messages())
            else:
                messages.append("验证码不对")
            self.render_message_widget(Message(messages=messages))


class LogoutHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.clear_cookie("user")
        self.goto_main_page()


handlers = [
    (r"/personal/self", SelfHandler),
    (r"/personal/valid", ValidHandler),
    (r"/personal/active", ActiveHandler),
    (r"/personal/resetPwd", ResetPwdHandler),
    (r"/personal/login", LoginHandler),
    (r"/personal/register", RegisterHandler),
    (r"/personal/logout", LogoutHandler),
]