#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms.fields import Field
from wtforms.widgets import TextInput
from wtforms import Form, TextField, validators, TextAreaField, HiddenField, SelectField, RadioField, IntegerField
from webadm.forms import eth_choices, time_of_day_choices

class IpListField(Field):
    widget = TextInput()

    def _value(self):
        if self.data:
            return u';'.join(self.data)
        return u''

    def process_formdata(self, valuelist):
        if valuelist:
            self.data = [x.strip() for x in valuelist[0].split(';')]
        else:
            self.data = []


class BasicForm(Form):
    host_id = HiddenField()
    caption = TextField(u'名称', [validators.Required(message=u'名称不能为空')])
    details = TextAreaField(u'详细信息')


class WanForm(Form):
    host_id = HiddenField()
    device = SelectField(u'公网设备', choices=eth_choices)
    #ips = IpListField(u'公网IP列表', [validators.Required(message=u'公网IP列表不能为空')])
    ips = TextField(u'公网IP列表', [validators.Required(message=u'公网IP列表不能为空')])


class LanForm(Form):
    host_id = HiddenField()
    device = SelectField(u'内网设备', choices=eth_choices)
    net = TextField(u'网络标识', [validators.Required(message=u'网络标识不能为空'), validators.IPAddress(message=u'网络标识不正确')])


class AllowLanForm(Form):
    host_id = HiddenField()
    allow = RadioField(u'开启局域网功能', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)


class DmzForm(Form):
    host_id = HiddenField()
    device = SelectField(u'DMZ设备', choices=eth_choices)
    net = TextField(u'网络标识', [validators.Required(message=u'网络标识不能为空'), validators.IPAddress(message=u'网络标识不正确')])


class AllowDmzForm(Form):
    host_id = HiddenField()
    allow = RadioField(u'开启DMZ区功能', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)


class PingForm(Form):
    host_id = HiddenField()
    allow = RadioField(u'允许PING', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)

class NatBatchForm(Form):
    host_id = HiddenField()
    s_ip = SelectField(u'源ip')
    start_port = IntegerField(u'起始port', [validators.Required(message=u'源端口不能为空'),
                                     validators.NumberRange(min=1, max=65535, message=u'端口应该为整数，在1-65535之间')])
    end_port = IntegerField(u'截止port', [validators.Required(message=u'目的端口不能为空'),
                                      validators.NumberRange(min=1, max=65535, message=u'端口应该为整数，在1-65535之间')])
    protocol = RadioField(u'协议', choices=[('tcp', 'TCP'), ('udp', 'UDP')], default='tcp')
    d_ip = SelectField(u'目的ip')


class NatForm(Form):
    host_id = HiddenField()
    item_id = HiddenField()
    s_ip = SelectField(u'源ip')
    s_port = IntegerField(u'源port', [validators.Required(message=u'源端口不能为空'),
                                     validators.NumberRange(min=1, max=65535, message=u'端口应该为整数，在1-65535之间')])
    protocol = RadioField(u'协议', choices=[('tcp', 'TCP'), ('udp', 'UDP')], default='tcp')
    d_ip = SelectField(u'目的ip')
    d_port = IntegerField(u'目的port', [validators.Required(message=u'目的端口不能为空'),
                                      validators.NumberRange(min=1, max=65535, message=u'端口应该为整数，在1-65535之间')])


class InputForm(Form):
    host_id = HiddenField()
    item_id = HiddenField()
    s_ip = TextField(u'源ip', [validators.Required(message=u'来源IP地址不能为空')])
    protocol = RadioField(u'协议', choices=[('tcp', 'TCP'), ('udp', 'UDP')], default='tcp')
    d_ip = SelectField(u'目的ip')
    d_port = IntegerField(u'目的port', [validators.Required(message=u'目的端口不能为空'),
                                      validators.NumberRange(min=1, max=65535, message=u'端口应该为整数，在1-65535之间')])


class OutputForm(Form):
    host_id = HiddenField()
    item_id = HiddenField()
    domain = TextField(u'域名关键字', [validators.Required(message=u'域名关键字不能为空')])
    start = SelectField(u'开始时间', choices=time_of_day_choices())
    end = SelectField(u'截止时间', choices=time_of_day_choices())
    mon = RadioField(u'星期一', choices=[(1, u'开启'), (0, u'关闭')], default=1, coerce=int)
    tue = RadioField(u'星期二', choices=[(1, u'开启'), (0, u'关闭')], default=1, coerce=int)
    wed = RadioField(u'星期三', choices=[(1, u'开启'), (0, u'关闭')], default=1, coerce=int)
    thu = RadioField(u'星期四', choices=[(1, u'开启'), (0, u'关闭')], default=1, coerce=int)
    fri = RadioField(u'星期五', choices=[(1, u'开启'), (0, u'关闭')], default=1, coerce=int)
    sat = RadioField(u'星期六', choices=[(1, u'开启'), (0, u'关闭')], default=0, coerce=int)
    sun = RadioField(u'星期日', choices=[(1, u'开启'), (0, u'关闭')], default=0, coerce=int)


class OutputElseForm(Form):
    host_id = HiddenField()
    output = SelectField(u'出口规则', coerce=int)
    mac = SelectField(u'MAC地址', coerce=int)


class AllowBlackListForm(Form):
    host_id = HiddenField()
    allow = RadioField(u'上网拦截设置', choices=[(1, u'黑名单'), (0, u'白名单')], default=0, coerce=int)



  