#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, request, flash, redirect, url_for, g

from webadm.helpers import templated
from webadm.forms.site import TitleForm, AboutMeForm, AdminForm, CopyRightForm
from webadm.models.site import Site

site_page = Blueprint('site_page', __name__)

@site_page.route('/')
@templated('site/index.html')
def index():
    title_fm = TitleForm()
    admin_fm = AdminForm()
    copy_right_fm = CopyRightForm()
    about_me_fm = AboutMeForm()

    #site = Site.query.get(site_id)
    title_fm.title.data = g.site.title
    admin_fm.admin_user.data = g.site.admin_user
    admin_fm.admin_email.data = g.site.admin_email
    about_me_fm.about_me.data = g.site.about_me
    copy_right_fm.copy_right.data = g.site.copy_right

    return dict(title_fm=title_fm, admin_fm=admin_fm, copy_right_fm=copy_right_fm, about_me_fm=about_me_fm)


@site_page.route('/about_me', methods=['POST'])
def about_me():
    form = AboutMeForm(request.form)
    if form.validate():
        Site.query.set_about_me(form.about_me.data)
        flash(u'设置关于我们成功')
    return redirect(url_for('site_page.index'))


@site_page.route('/title', methods=['POST'])
def title():
    form = TitleForm(request.form)
    if form.validate():
        Site.query.set_title(form.title.data)
        flash(u'设置站点标题成功')
    return redirect(url_for('site_page.index'))


@site_page.route('/copy_right', methods=['POST'])
def copy_right():
    form = CopyRightForm(request.form)
    if form.validate():
        Site.query.set_copy_right(form.copy_right.data)
        flash(u'设置版权信息成功')
    return redirect(url_for('site_page.index'))


@site_page.route('/admin', methods=['POST'])
def admin():
    form = AdminForm(request.form)
    if form.validate():
        Site.query.set_admin(form.admin_user.data, form.admin_email.data)
        flash(u'设置管理员成功')
    return redirect(url_for('site_page.index'))
  