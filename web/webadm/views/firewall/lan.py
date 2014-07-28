#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for, flash

from webadm.models.host import Host
from webadm.models.firewall import Firewall
from webadm.forms.firewall import AllowLanForm, LanForm
from webadm.helpers import templated

ff_lan_page = Blueprint('ff_lan_page', __name__)


@ff_lan_page.route('/save', methods=['POST'])
def on_lan():
    form = LanForm(request.form)
    host_id = form.host_id.data
    if form.validate():
        if  Firewall.query.save_lan(host_id, form.device.data, form.net.data):
            flash(u'保存主机 %s 局域网信息成功' % host_id)
        else:
            flash(u'主机 %s 不支持没有启用防火墙或是不存在' % host_id)
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.index', host_id=host_id))


@ff_lan_page.route('/allow', methods=['POST'])
def on_allow():
    form = AllowLanForm(request.form)
    host_id = form.host_id.data

    if form.validate():
        allow = form.allow.data
        if  Firewall.query.save_allow_lan(host_id, allow):
            flash((allow and u'主机 %s 启用局域网成功'  or u'主机 %s 禁用局域网成功') % host_id)
        else:
            flash(u'主机 %s 不支持没有启用防火墙或是不存在' % host_id)
    return redirect(url_for('.index', host_id=host_id))


@ff_lan_page.route('/<int:host_id>')
@templated('/firewall/lan.html')
def index(host_id):
    h = Host.query.get(host_id)
    allow = False

    fm_allow = None
    fm_lan = None
    if h:
        if h.allow_firewall:
            if h.firewall:
                allow = True

                fm_allow = AllowLanForm()
                fm_allow.host_id.data = h.id
                fm_allow.allow.data = h.firewall.allow_lan

                if h.firewall.allow_lan:
                    fm_lan = LanForm()
                    fm_lan.host_id.data = h.id
                    fm_lan.device.data = h.firewall.lan_device
                    fm_lan.net.data = h.firewall.lan_net
            else:
                flash(u'主机 %s 尚未初始化' % h)
        else:
            flash(u'主机 %s 防火墙未启用' % h)
    else:
        flash(u'主机 %s 不存在')

    return dict(fm_allow=fm_allow, fm_lan=fm_lan, host_id=host_id, allow=allow)


@ff_lan_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()
  