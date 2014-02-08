__author__ = 'zhengjitang@gmail.com'

from wtforms import TextField, validators, TextAreaField, IntegerField, BooleanField, PasswordField, HiddenField, \
    SelectField

from brahma.web import Form, HtmlField


class TimerForm(Form):
    act = HiddenField()
    clock = SelectField("时刻", coerce=int, choices=[(i, "%02d:00" % i) for i in range(0, 24)])


class FriendLinkForm(Form):
    flid = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    domain = TextField("域名", validators=[validators.Required()])
    logo = TextField("图标")


class ValidCodeForm(Form):
    code = TextField("文件名")


class AdvertForm(Form):
    aid = HiddenField()
    script = TextAreaField("脚本")


class ProtocolForm(Form):
    content = TextAreaField("用户协议")


class ContentForm(Form):
    content = HtmlField()


class InfoForm(Form):
    domain = TextField("域名", validators=[validators.Required()])
    title = TextField("标题", validators=[validators.Required()])
    keywords = TextField("关键字", validators=[validators.Required()])
    description = TextAreaField("说明")


class SmtpForm(Form):
    host = TextField("SMTP主机", validators=[validators.Required()])
    port = IntegerField("SMTP端口", validators=[validators.Required(), validators.NumberRange(1, 65535)])
    ssl = BooleanField("启用SSL")
    username = TextField("SMTP用户", validators=[validators.Required()])
    password = PasswordField("SMTP密码", validators=[validators.Required(), validators.EqualTo("rePassword")])
    rePassword = PasswordField("再输一次", validators=[validators.Required()])
    bcc = TextField("抄送")


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

