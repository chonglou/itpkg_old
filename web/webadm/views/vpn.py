#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for, flash

from webadm.helpers import templated
from webadm.models.host import Host
from webadm.models.vpn import Vpn, VpnUser
from webadm.forms.vpn import MySQLForm, UserForm
from webadm.models.employee import Employee
from webadm.utils.vpn import VpnMysqlHelper


vpn_page = Blueprint('vpn_page', __name__)

@vpn_page.route('/')
@templated('vpn/index.html')
def index():
    return dict(act='user')


@vpn_page.route('/setup/<int:host_id>', methods=['GET', 'POST'])
@templated('vpn/setup.html')
def setup(host_id):
    form = MySQLForm(request.form)
    if request.method == 'POST':
        if form.validate():
            id = form.host_id.data
            db_port = form.db_port.data
            if not db_port:
                db_port = 3306
            if Vpn.query.set(id, form.caption.data, form.details.data, form.db_host.data, db_port, form.db_name.data,
                             form.db_user.data, form.db_password.data):
                flash(u'保存数据库连接 %s 记录成功' % id)
            else:
                flash(u'主机 %s 不存在或者尚未开启VPN功能' % id)
            return redirect(url_for('.setup', host_id=id))

    h = Host.query.get(host_id)
    if h:
        if h.allow_vpn:
            form.host_id.data = h.id
            if h.vpn:
                form.caption.data = h.vpn.caption
                form.details.data = h.vpn.details
                form.db_host.data = h.vpn.db_host
                form.db_port.data = h.vpn.db_port
                form.db_name.data = h.vpn.db_name
                form.db_user.data = h.vpn.db_user
        else:
            form = None
            flash(u'主机 %s 尚未开启VPN功能' % host_id)
    else:
        form = None
        flash(u'主机 %s 不存在' % host_id)

    return dict(form=form, host_id=host_id, sql=VpnMysqlHelper.create_table_sql)


@vpn_page.route('/user/<int:host_id>/<name>/delete')
@templated('msg.html')
def delete_user(host_id, name):
    msgs = []
    h = Host.query.get(host_id)
    next = url_for('.setup', host_id=host_id)
    if h:
        if h.allow_vpn:
            if h.vpn:
                try:
                    conn = VpnMysqlHelper(h.vpn.db_host, h.vpn.db_name, h.vpn.db_user, h.vpn.db_password)
                    conn.delete_vpn_user(name)
                    conn.close()
                    msgs.append(u'删除VPN用户 %s 成功' % name)
                    next = url_for('.user', host_id=host_id)
                except Exception, e:
                    msgs.append(u'删除用户 %s 出错 数据库错误 %s ' % (name, e))
            else:
                msgs.append(u'请先设置数据库连接')
        else:
            msgs.append(u'主机 %s 尚未开启VPN服务' % h.id)
            next = url_for('host_page.edit', host_id=h.id)
    else:
        msgs.append(u'主机 %s 不存在' % host_id)
    return dict(page='vpn', msgs=msgs, next=next)


@vpn_page.route('/user/<int:host_id>/<name>')
@vpn_page.route('/user/<int:host_id>', methods=['POST', 'GET'])
@templated('vpn/user.html')
def user(host_id, name=None):
    form = UserForm(request.form)
    form.name.choices = [(u.nick_name, '%s' % u) for u in Employee.query.all()]

    if request.method == 'POST':
        if form.validate():
            id = form.host_id.data
            h = Host.query.get(id)
            u = VpnUser(form.name.data, form.password.data, form.active.data)
            try:
                conn = VpnMysqlHelper(h.vpn.db_host, h.vpn.db_name, h.vpn.db_user, h.vpn.db_password)
                conn.store_vpn_user(u)
                conn.close()
                flash(u'保存VPN用户 %s 成功' % u)
            except Exception, e:
                flash(u'数据库出错 %s ' % e)
            return redirect(url_for('.user', host_id=host_id))

    h = Host.query.get(host_id)
    users = []
    if h:
        if h.allow_vpn:
            if h.vpn:
                form.host_id.data = h.id
                try:
                    conn = VpnMysqlHelper(h.vpn.db_host, h.vpn.db_name, h.vpn.db_user, h.vpn.db_password)
                    users = conn.list_all_vpn_users()
                    if name:
                        u = conn.select_vpn_user(name)
                        if u:
                            form.active.data = u.active
                            form.name.data = u.name
                    conn.close()
                except Exception, e:
                    form = None
                    flash(u'数据库出错 %s' % e)
            else:
                form = None
                flash(u'尚未设置数据库连接')
        else:
            form = None
            flash(u'主机 %s 不支持VPN' % h)
    else:
        form = None
        flash(u'主机 %s 不存在' % host_id)
    return dict(form=form, host_id=host_id, users=users)


@vpn_page.route('/act', methods=['POST'])
def action():
    return redirect(url_for('vpn_page.%s' % request.form['act'], host_id=request.form['id']))

@vpn_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_vpn == True).all()

  