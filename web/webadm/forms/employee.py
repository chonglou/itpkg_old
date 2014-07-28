#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, TextField, validators, TextAreaField, HiddenField, SelectField
from webadm.forms import nick_name_check

class UnitForm(Form):
    unit_id = HiddenField()
    caption = TextField(u'名称', [validators.Required(message=u'名称不能为空')])
    details = TextAreaField(u'详细信息')


class EmployeeForm(Form):
    user_id = HiddenField()
    nick_name = TextField(u'昵称', [validators.Required(message=u'昵称不能为空'), nick_name_check()])
    real_name = TextField(u'姓名', [validators.Required(message=u'姓名不能为空')])
    unit_id = SelectField(u'部门', coerce=int)
    details = TextAreaField(u'详细信息')


class ContactForm(Form):
    user_id = HiddenField()
    qq = TextField(u"QQ")
    msn = TextField(u"MSN")
    email = TextField(u"邮箱")
    phone = TextField(u"手机")
    tel = TextField(u"座机")
    fax = TextField(u"传真")
    home = TextField(u"家庭住址")
    work = TextField(u"工作地点")
