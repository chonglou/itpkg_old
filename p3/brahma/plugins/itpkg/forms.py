__author__ = 'zhengjitang@gmail.com'

from wtforms import validators, TextField, HiddenField, PasswordField, SelectField, BooleanField, IntegerField, \
    RadioField

from brahma.plugins.itpkg.models import WanFlag, RouterFlag
from brahma.web import Form, HtmlField, ListField


def ip_choices(net):
    return [(i, "%s.%s" % (net, i)) for i in range(2, 254)]


def _speed_choice():
    return [(50, "50K"), (100, "100K"), (200, "200K"), (300, "300K"), (500, "500K"), (800, "800K"), (1000, "1M"),
            (1500, "1.5M"), (2000, "2M"), (5000, "5M")]


def _time_choices():
    v = []
    for i in range(0, 24):
        v.append(("%02d:00" % i, "%02d:00" % i))
        v.append(("%02d:30" % i, "%02d:30" % i))
    return v


def _protocol_choices():
    return [("tcp", "TCP"), ("udp", "UDP")]


class LimitForm(Form):
    id = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    upMax = SelectField("最大上传", choices=_speed_choice(), coerce=int)
    downMax = SelectField("最大下载", choices=_speed_choice(), coerce=int)
    upMin = SelectField("最小上传", choices=_speed_choice(), coerce=int)
    downMin = SelectField("最小下载", choices=_speed_choice(), coerce=int)


class NatForm(Form):
    id = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    sport = IntegerField("来源端口")
    protocol = RadioField("协议", choices=_protocol_choices())
    dip = SelectField("目的地址", coerce=int)
    dport = IntegerField("目的端口")


class OutputForm(Form):
    id = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    keyword = TextField("关键字", validators=[validators.Required()])
    begin = SelectField("起始时间", choices=_time_choices())
    end = SelectField("截止时间", choices=_time_choices())
    weekdays = ListField("生效日期", choices=[
        ("mon", "星期一"),
        ("tue", "星期二"),
        ("wed", "星期三"),
        ("thu", "星期四"),
        ("fri", "星期五"),
        ("sat", "星期六"),
        ("sun", "星期日")
    ])


class InputForm(Form):
    id = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    port = IntegerField("端口", validators=[validators.Required()])
    protocol = RadioField("协议", choices=_protocol_choices())


class DhcpForm(Form):
    mac = SelectField("MAC", coerce=int)
    ip = SelectField("IP", coerce=int)
    flag = BooleanField("绑定")


class DeviceForm(Form):
    act = HiddenField()
    id = HiddenField()
    user = SelectField("用户", coerce=int)
    limit = SelectField("限速规则", coerce=int)
    enable = BooleanField("启用")
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
    flag = SelectField("类型", choices=[(RouterFlag.ARCH_LINUX_OLD, '旧版ArchLinux')])
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