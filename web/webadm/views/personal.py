#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, request, flash, session, redirect, url_for

from webadm.helpers import templated
from webadm.models.site import User, Log, Note
from webadm.forms.personal import LoginForm, SetPasswordForm, NoteForm

personal_page = Blueprint('personal_page', __name__)


@personal_page.route('/')
def index():
    return redirect(url_for('personal_page.log'))


@personal_page.route('/log/clear')
def log_clear():
    Log.query.clear()
    Log.query.add(Log(session['who'], u'清除日志'))
    return redirect(url_for('personal_page.log'))


@personal_page.route('/log/<int:log_id>/delete')
def log_delete(log_id):
    if Log.query.delete(log_id):
        flash(u'删除日志 %s 成功' % log_id)
    else:
        flash(u'日志 %s 不存在' % log_id)
    return redirect(url_for('personal_page.log'))


@personal_page.route('/log')
@templated('personal/log.html')
def log():
    return dict(logs=Log.query.all())


@personal_page.route('/note/<int:note_id>/delete')
def note_delete(note_id):
    if Note.query.delete(note_id):
        flash(u'删除笔记 %s 成功' % note_id)
    else:
        flash(u'笔记 %s 不存在' % note_id)
    return redirect(url_for('personal_page.note'))


@personal_page.route('/note', methods=['GET', 'POST'])
@personal_page.route('/note/<int:note_id>')
@templated('personal/note.html')
def note(note_id=None):
    form = NoteForm(request.form)
    if request.method == 'POST':
        if form.validate():
            content = form.content.data
            note_id = form.note_id.data
            if note_id:
                Note.query.set_content(note_id, content)
                flash(u'保存笔记 %s 成功' % note_id)
            else:
                Note.query.add(Note(session['who'], None, content))
                flash(u'保存新笔记成功')
            return redirect(url_for('.note'))
    if note_id:
        n = Note.query.get(note_id)
        if  n:
            form.note_id.data = note_id
            form.content.data = n.content
        else:
            flash(u'记录 %s 不存在' % note_id)

    return dict(form=form, notes=Note.query.filter(Note.author_id == session['who']).all())


@personal_page.route('/set_pwd', methods=('GET', 'POST'))
@templated('personal/set_pwd.html')
def set_pwd():
    form = SetPasswordForm(request.form)
    if request.method == 'POST' and form.validate():
        old_pwd = form.old_pwd.data
        new_pwd = form.new_pwd.data
        if old_pwd == new_pwd:
            flash(u'新旧密码相同')
        else:
            id = session['who']
            user = User.query.get(id)
            if old_pwd == user.password:
                User.query.set_password(id, new_pwd)
                flash(u'修改密码成功，请妥善保管！')
            else:
                flash(u'旧密码输入错误')
    return dict(form=form)


@personal_page.route('/login', methods=('GET', 'POST'))
@templated('personal/login.html')
def login():
    form = LoginForm(request.form)
    if request.method == 'POST' and form.validate():
        nick_name = form.nick_name.data
        password = form.password.data
        user = User.query.authenticate(nick_name, password)
        if user:
            session['who'] = user.id
            session['nick_name'] = user.nick_name
            Log.query.add(Log(user.id, u'%s 成功登录' % nick_name))
            return redirect(url_for('personal_page.index'))
        else:
            Log.query.add(Log(None, u"%s 尝试使用密码 %s 登录" % (nick_name, password)))
            flash(u'账户密码不匹配')
    return dict(form=form)


@personal_page.route('/logout')
def logout():
    who = session['who']
    nick_name = session['nick_name']
    session.pop('who', None)
    session.pop('nick_name', None)
    Log.query.add(Log(who, u'%s退出登录' % nick_name))
    return redirect(url_for('personal_page.index'))


  