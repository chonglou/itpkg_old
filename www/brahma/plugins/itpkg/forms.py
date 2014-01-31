__author__ = 'zhengjitang@gmail.com'

from wtforms import validators, TextField, HiddenField, PasswordField, SelectField, BooleanField

from brahma.web import Form, HtmlField


class DeviceBindForm(Form):
    mac = SelectField("MAC", coerce=int)
    ip = SelectField("IP", coerce=int)
    flag = BooleanField("绑定")


class DeviceInfoForm(Form):
    act = HiddenField()
    id = HiddenField()
    user = SelectField("用户")
    details = HtmlField("详细信息")


class WanForm(Form):
    act = HiddenField()
    flag = SelectField("WAN类型", choices=[('static', "固定IP"), ])
    ip = TextField("IP地址")
    netmask = TextField("掩码")
    gateway = TextField("网关")
    dns1 = TextField("DNS1", validators=[validators.Required()])
    dns2 = TextField("DNS2", validators=[validators.Required()])


class LanForm(Form):
    act = HiddenField()
    net = TextField("ID", validators=[validators.Required()])
    domain = TextField("域", validators=[validators.Required()])


class InitForm(Form):
    flag = SelectField("类型", choices=[('ArchLinux', 'ArchLinux'), ('DIR-615', 'DIR-615')])
    host = TextField("主机&端口", validators=[validators.Required()])
    password = PasswordField("ROOT密码")
    wanMac = TextField("WAN MAC")
    #('dhcp', '动态分配')
    wanFlag = SelectField("WAN类型", choices=[('static', "固定IP"), ])
    wanIp = TextField("WAN IP地址")
    wanNetmask = TextField("WAN 掩码")
    wanGateway = TextField("WAN 网关")
    wanDns1 = TextField("WAN DNS1", validators=[validators.Required()])
    wanDns2 = TextField("WAN DNS2", validators=[validators.Required()])
    lanMac = TextField("LAN MAC")
    lanNet = TextField("LAN ID", validators=[validators.Required()])
    lanDomain = TextField("LAN 域", validators=[validators.Required()])


class InfoForm(Form):
    iid = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    details = HtmlField("详情")