#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, TextField, validators, TextAreaField, HiddenField, SelectField, RadioField

class Dhcpd4BasicForm(Form):
    host_id = HiddenField()
    caption = TextField(u'名称', [validators.Required(message=u'名称不能为空')])
    details = TextAreaField(u'详细信息')


class Dhcpd4SetupForm(Form):
    host_id = HiddenField()
    domain = TextField(u'子域', [validators.Required(message=u'局域网子域不能为空')])
    lan_net = TextField(u'子网IP', [validators.Required(message=u'子网IP不能为空'), validators.IPAddress(message=u'子网IP格式不正确')])
    dns_1 = TextField(u'默认DNS',
        [validators.Required(message=u'默认DNS不能为空'), validators.IPAddress(message=u'默认DNS IP地址格式不正确')])
    dns_2 = TextField(u'备用DNS',
        [validators.Required(message=u'默认DNS不能为空'), validators.IPAddress(message=u'备用DNS IP地址格式不正确')])


class MacIpBindForm(Form):
    host_id = HiddenField()
    mac_id = SelectField(u'MAC地址', coerce=int)
    ip = SelectField(u'IP地址')
    bind = RadioField(u'IP类型', choices=[(1, u'固定'), (0, u'动态')], default=0, coerce=int)

