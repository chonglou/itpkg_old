#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, TextField, validators, TextAreaField, HiddenField, SelectField

class BasicForm(Form):
    host_id = HiddenField()
    caption = TextField(u'名称', [validators.Required(message=u'名称不能为空')])
    details = TextAreaField(u'详细信息')


class SetupForm(Form):
    host_id = HiddenField()
    named_id = HiddenField()
    listen_on_ips = TextField(u'监听IP地址', [validators.Required(message=u'监听IP地址不能为空')])
    forwarders = TextField(u'上级DNS地址', [validators.Required(message=u'上级DNS的ip地址不能为空')])
    controls = TextField(u'控制器监听地址',
        [validators.Required(message=u'控制监听地址不能为空'), validators.IPAddress(message=u'控制器ip地址不正确')])


class ZoneForm(Form):
    host_id = HiddenField()
    named_id = HiddenField()
    zone_id = HiddenField()
    domain = TextField(u'根域名', [validators.Required(message=u'根域名不能为空')])


class AForm(Form):
    host_id = HiddenField()
    zone_id = SelectField(u'主域名', coerce=int)
    prefix = TextField(u'子域名', [validators.Required(message=u'子域名不能为空')])
    target = TextField(u'IP地址', [validators.Required(message=u'IP地址不能为空'), validators.IPAddress(message=u'IP地址不正确')])


class MXForm(Form):
    host_id = HiddenField()
    ns_id = SelectField(u'邮件域', coerce=int)
    priority = SelectField(u'权重', choices=[(i, i) for i in range(1, 11)], coerce=int)
  