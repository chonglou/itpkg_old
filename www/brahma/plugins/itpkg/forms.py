__author__ = 'zhengjitang@gmail.com'

from wtforms import validators, TextField, HiddenField, PasswordField, SelectField

from brahma.web import Form, HtmlField


class InitForm(Form):
    flag = SelectField("类型", choices=[('ArchLinux', 'ArchLinux'), ('DIR-615', 'DIR-615')])
    host = TextField("主机&端口", validators=[validators.Required()])
    password = PasswordField("ROOT密码")
    wanMac = TextField("WAN MAC")
    wanFlag = SelectField("WAN类型", choices=[('static', "固定IP"), ('dhcp', '动态分配')])
    wanIp = TextField("WAN IP地址")
    wanNetmask = TextField("WAN 掩码")
    wanGateway = TextField("WAN 网关")
    wanDns1 = TextField("WAN DNS1", validators=[validators.Required()])
    wanDns2 = TextField("WAN DNS2", validators=[validators.Required()])
    lanMac = TextField("LAN MAC")
    lanNet = TextField("LAN 网络ID", validators=[validators.Required()])


class InfoForm(Form):
    iid = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    details = HtmlField("详情")