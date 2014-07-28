#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, TextField, validators, TextAreaField, HiddenField, RadioField, SelectField

class HostForm(Form):
    host_id = HiddenField()
    caption = TextField(u'名称', [validators.Required(message=u'名称不能为空')])
    details = TextAreaField(u'详细信息')
    listen_ip = TextField(u'监听IP',
        [validators.Required(message=u'监听IP地址不能为空'), validators.IPAddress(message=u'监听IP地址格式不正确')])
    manager_ip = TextField(u'管理机IP',
        [validators.Required(message=u'管理主机IP地址不能为空'), validators.IPAddress(message=u'管理主机IP地址格式不正确')])
    allow_dhcpd4 = RadioField(u'DHCP服务', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)
    allow_named = RadioField(u'DNS服务', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)
    allow_firewall = RadioField(u'防火墙', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)
    allow_vpn = RadioField(u'VPN服务', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)
    allow_mail = RadioField(u'邮件服务', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)


class MacInfoForm(Form):
    mac_id = HiddenField()
    host_id = HiddenField()
    mac = SelectField(u'网卡MAC地址')
    ip = SelectField(u'IP地址')
    allow = RadioField(u'允许访问网络', choices=[(1, u'允许'), (0, u'禁止')], default=0, coerce=int)
    employee_id = SelectField(u'雇员', coerce=int)

  