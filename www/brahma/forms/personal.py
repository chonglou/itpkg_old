__author__ = 'zhengjitang@gmail.com'

from wtforms import TextField, validators, PasswordField,TextAreaField
from brahma.web import Form, AgreementField
from brahma.cache import get_site_info


class ContactForm(Form):
    username = TextField("用户名", validators=[validators.Required()])
    logo = TextField("头像")
    qq = TextField("QQ")
    email = TextField("Email")
    website = TextField("个人主页")
    wechat = TextField("微信")
    weibo = TextField("微博")
    address = TextField("住址")
    fax = TextField("传真")
    tel = TextField("电话")
    details = TextAreaField("个人介绍")


class SetPwdForm(Form):
    oldPassword = PasswordField("旧密码", validators=[validators.Required()])
    password = PasswordField("新密码",
                             validators=[validators.Required(), validators.EqualTo("rePassword")])
    rePassword = PasswordField("再输一次")


class LoginForm(Form):
    email = TextField("邮箱", validators=[validators.Required(), validators.Email()])
    password = PasswordField("密码", validators=[validators.Required()])


class RegisterForm(Form):
    username = TextField("用户名", validators=[validators.Required()])
    email = TextField("电子邮箱", validators=[validators.Required(), validators.Email()])
    password = PasswordField("登录密码",
                             validators=[validators.Required(), validators.EqualTo("rePassword")])
    rePassword = PasswordField("再输一次")
    agree = AgreementField(get_site_info("protocol"))


class ActiveForm(Form):
    email = TextField("电子邮箱", validators=[validators.Required(), validators.Email()])
    agree = AgreementField(get_site_info("protocol"))


class ResetPwdForm(Form):
    email = TextField("电子邮箱", validators=[validators.Required(), validators.Email()])
    password = PasswordField("登录密码",
                             validators=[validators.Required(), validators.EqualTo("rePassword")])
    rePassword = PasswordField("再输一次")