#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, TextField, validators, TextAreaField, HiddenField, IntegerField, PasswordField, SelectField, RadioField

class MySQLForm(Form):
    host_id = HiddenField()
    caption = TextField(u'名称', [validators.Required(message=u'名称内容不能为空')])
    details = TextAreaField(u'详细信息')
    db_host = TextField(u'主机', [validators.Required(message=u'数据库主机不能为空'), validators.IPAddress(message=u'IP地址格式不正确')])
    db_port = IntegerField(u'端口', [validators.NumberRange(min=1, max=65535, message=u'端口号不正确')], default=3306)
    db_name = TextField(u'数据库', [validators.Required(message=u'名称内容不能为空')])
    db_user = TextField(u'用户', [validators.Required(message=u'数据库账户不能为空')])
    db_password = PasswordField(u'密码', [validators.Required(message=u'数据库密码不能为空')])


class UserForm(Form):
    host_id = HiddenField()
    name = SelectField(u'雇员')
    active = RadioField(u'状态', choices=[(0, u'禁用'), (1, u'启用')], coerce=int, default=0)
    password = PasswordField(u'密码',
        [validators.Required(message=u'密码不能为空'), validators.Length(min=6, max=255, message=u'密码在6-255位之间')])
    

  