#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, TextField, validators, TextAreaField

class TitleForm(Form):
    title = TextField(u'标题', [validators.Required(message=u'标题内容不能为空')])


class AboutMeForm(Form):
    about_me = TextAreaField(u'内容', [validators.Required(message=u'关于我们内容不能为空')])


class AdminForm(Form):
    admin_user = TextField(u'姓名', [validators.Required(message=u'管理员姓名不能为空')])
    admin_email = TextField(u'邮箱', [validators.Required(message=u'管理员邮箱不能为空')])


class CopyRightForm(Form):
    copy_right = TextField(u'版权信息', [validators.Required(message=u'版权信息不能为空')])
