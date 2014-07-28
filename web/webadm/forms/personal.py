#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, TextField, PasswordField, validators, TextAreaField, HiddenField

class LoginForm(Form):
    nick_name = TextField(u'帐号', [validators.Length(min=3, max=12, message=u'账户长度在3和12之间')])
    password = PasswordField(u'密码', [validators.Length(min=6, max=25, message=u'密码长度在6和25之间')])


class SetPasswordForm(Form):
    old_pwd = PasswordField(u'旧密码', [validators.Length(min=6, max=25, message=u'密码长度在6和25之间')])
    re_pwd = PasswordField(u'新密码', [validators.Length(min=6, max=25, message=u'密码长度在6和25之间'),
                                    validators.EqualTo('new_pwd', message=u'两次密码输入不一致')])
    new_pwd = PasswordField(u'再输一遍', [validators.Length(min=6, max=25, message=u'密码长度在6和25之间')])


class NoteForm(Form):
    content = TextAreaField(u'内容', [validators.Required(message=u'日志内容不能为空')])
    note_id = HiddenField()
  