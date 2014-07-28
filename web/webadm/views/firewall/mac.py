#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, redirect, url_for, request, flash, g

from webadm.utils.rpc import Rpc
from webadm.models.host import Host, MacInfo
from webadm.models.employee import Employee
from webadm.forms.host import MacInfoForm
from webadm.helpers import templated

ff_mac_page = Blueprint('ff_mac_page', __name__)


@ff_mac_page.route('/<int:host_id>/<int:mac_id>')
@ff_mac_page.route('/<int:host_id>')
@templated('firewall/mac.html')
def index(host_id, mac_id=None):
    h = Host.query.get(host_id)
    allow = False
    macs = []
    if h:
        if h.allow_firewall:
            if h.firewall:
                if h.firewall.allow_lan:
                    allow = True
                    macs = MacInfo.query.filter(MacInfo.host_id == host_id).all()
                else:
                    flash(u'主机 %s 防火墙尚未启用局域网功能' % h)
            else:
                flash(u'主机 %s 防火墙尚未初始化,请使用左侧栏［控制面板 => 首选项］设置基本信息' % host_id)
        else:
            flash(u'主机 %s 防火墙尚未启用' % host_id)
    else:
        flash(u'主机 %s 不存在' % host_id)

    form = None
    if mac_id:
        m = MacInfo.query.get(mac_id)
        if m:
            form = MacInfoForm()
            form.mac_id.data = m.id
            form.host_id.data = m.host_id
            form.mac.choices = [(m.mac, m.mac)]
            form.ip.choices = [(m.ip, m.ip)]
            #form.host_name.choices = [(m.host_name, m.host_name)]
            form.allow.data = m.allow
            form.employee_id.choices = [(u.id, u) for u in Employee.query.order_by(Employee.nick_name).all()]
            form.employee_id.data = m.employee_id

        else:
            flash(u'Mac地址 %s 不存在' % mac_id)

    return dict(macs=macs, host_id=host_id, allow_lan=allow, form=form)


@ff_mac_page.route('/delete/<int:host_id>/<int:mac_id>')
def delete(host_id, mac_id):
    if MacInfo.query.delete(mac_id):
        flash(u'删除 %s 成功' % mac_id)
    else:
        flash(u'MAC记录 %s 不存在' % mac_id)
    return redirect(url_for('ff_mac_page.index', host_id=host_id))


@ff_mac_page.route('/save', methods=['POST'])
def save():
    host_id = request.form['host_id']
    mac_id = request.form['mac_id']

    if 'employee_id' in request.form.keys():
        allow = request.form['allow'] == '1'
        employee_id = request.form['employee_id']

        if MacInfo.query.set(mac_id, allow=allow, employee_id=employee_id):
            flash(u'保存MAC信息 %s 成功' % mac_id)
        else:
            flash(u'MAC信息 %s 不存在' % mac_id)

    else:
        flash(u'雇员信息不存在')

    return redirect(url_for('ff_mac_page.index', host_id=host_id, mac_id=mac_id))


@ff_mac_page.route('/sync/<int:host_id>')
@templated('msg.html')
def sync(host_id):
    h = Host.query.get(host_id)
    if h:
        if h.firewall:
            if h.firewall.allow_lan:
                if h.firewall.lan_device:
                    rpc = Rpc(h.listen_ip)
                    try:
                        macs = rpc.arp_sync(h.firewall.lan_device)
                        add, edit = MacInfo.query.sync(host_id, macs)
                        flash(u'共扫描到%s条记录' % len(macs))
                        flash(u'新增%s条到数据库' % add)
                        flash(u"修正%s条ip错误信息" % edit)
                    except Exception, e:
                        flash(e.message)
                else:
                    flash(u'主机 %s 未设置局域网网卡' % h)
            else:
                flash(u'主机 %s 未启用局域网' % h)
        else:
            flash(u'防火墙尚未初始化')
    return redirect(url_for('.index', host_id=host_id))


@ff_mac_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()