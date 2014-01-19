__author__ = 'zhengjitang@gmail.com'

from wtforms import TextField, validators, TextAreaField, IntegerField, BooleanField, PasswordField
from brahma.web import Form


class InstallForm(Form):
    siteDomain = TextField("站点域名", validators=[validators.Required()])
    siteTitle = TextField("站点标题", validators=[validators.Required()])
    siteKeywords = TextField("站点关键字", validators=[validators.Required()])
    siteDescription = TextAreaField("站点说明")
    managerEmail = TextField("管理员邮箱", validators=[validators.Email(), validators.Required()])
    managerPassword = PasswordField("管理员密码",
                                    validators=[validators.Required(), validators.EqualTo("managerRePassword")])
    managerRePassword = PasswordField("再输一次")
    smtpHost = TextField("SMTP主机", validators=[validators.Required()], default="smtp.qq.com")
    smtpPort = IntegerField("SMTP端口", validators=[validators.Required(), validators.NumberRange(1, 65535)], default=465)
    smtpSsl = BooleanField("启用SSL", default=True)
    smtpUsername = TextField("SMTP用户", validators=[validators.Required()])
    smtpPassword = PasswordField("SMTP密码", validators=[validators.Required(), validators.EqualTo("smtpRePassword")])
    smtpRePassword = PasswordField("再输一次", validators=[validators.Required()])
    smtpBcc = TextField("抄送")

