#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for, flash

from webadm.models.host import Host
from webadm.models.firewall import Firewall
from webadm.forms.firewall import WanForm, PingForm
from webadm.helpers import templated

ff_wan_page = Blueprint('ff_wan_page', __name__)

@ff_wan_page.route('/save', methods=['POST'])
def on_wan():
    form = WanForm(request.form)
    host_id = form.host_id.data
    if form.validate():
        if  Firewall.query.save_wan(host_id, form.device.data, form.ips.data):
            flash(u'保存防火墙 %s 公网网卡信息成功' % host_id)
        else:
            flash(u'主机 %s 不支持没有启用防火墙或是不存在' % host_id)
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.index', host_id=host_id))


@ff_wan_page.route('/ping', methods=['POST'])
def on_ping():
    form = PingForm(request.form)
    host_id = form.host_id.data

    if form.validate():
        allow = form.allow.data
        if  Firewall.query.save_ping(host_id, allow):
            flash((allow and u'允许主机 %s 公网ping成功'  or u'禁止主机 %s 公网ping成功') % host_id)
        else:
            flash(u'主机 %s 不支持没有启用防火墙或是不存在' % host_id)
    return redirect(url_for('.index', host_id=host_id))


@ff_wan_page.route('/<int:host_id>')
@templated('/firewall/wan.html')
def index(host_id):
    h = Host.query.get(host_id)
    allow = False
    fm_ping = None
    fm_wan = None
    if h:
        if h.allow_firewall:
            if h.firewall:
                allow = True

                fm_wan = WanForm()
                fm_wan.device.data = h.firewall.wan_device
                fm_wan.ips.data = h.firewall.wan_ips
                fm_wan.host_id.data = h.id

                fm_ping = PingForm()
                fm_ping.host_id.data = h.id
                fm_ping.allow.data = h.firewall.allow_ping
            else:
                flash(u'主机 %s 尚未初始化' % h)
        else:
            flash(u'主机 %s 防火墙未启用' % h)
    else:
        flash(u'主机 %s 不存在')

    return dict(fm_ping=fm_ping, fm_wan=fm_wan, host_id=host_id, allow=allow)


@ff_wan_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()
  