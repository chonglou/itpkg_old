__author__ = 'zhengjitang@gmail.com'

from wtforms import TextField, validators, PasswordField
from brahma.forms import Form,AgreementField
from brahma.env import cache_call

@cache_call("site/protocol")
def get_protocol():
    from brahma.store.site import SiteDao
    return SiteDao.get("site.protocol")


class LoginForm(Form):
    email = TextField("邮箱", validators=[validators.Required(), validators.Email()])
    password = PasswordField("密码", validators=[validators.Required()])


class RegisterForm(Form):
    username = TextField("用户名", validators=[validators.Required()])
    email = TextField("电子邮箱", validators=[validators.Required(), validators.Email()])
    password = PasswordField("登录密码",
                             validators=[validators.Required(), validators.EqualTo("rePassword")])
    rePassword = PasswordField("再输一次")
    agree = AgreementField(get_protocol())


class ActiveForm(Form):
    email = TextField("电子邮箱", validators=[validators.Required(), validators.Email()])


class ResetPwdForm(Form):
    email = TextField("电子邮箱", validators=[validators.Required(), validators.Email()])
    password = PasswordField("登录密码",
                             validators=[validators.Required(), validators.EqualTo("rePassword")])
    rePassword = PasswordField("再输一次")