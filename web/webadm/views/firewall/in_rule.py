#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for, flash

from webadm.models.host import Host
from webadm.models.firewall import  InputRule
from webadm.forms.firewall import InputForm
from webadm.helpers import templated

ff_in_page = Blueprint('ff_in_page', __name__)


@ff_in_page.route('/save', methods=['POST'])
def save():
    form = InputForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.d_ip.choices = [(ip, ip) for ip in h.firewall.wan_ip_list()]

    if form.validate():
        firewall_id = h.firewall_id
        s_ip = form.s_ip.data
        protocol = form.protocol.data
        d_ip = form.d_ip.data
        d_port = form.d_port.data
        item_id = form.item_id.data

        if InputRule.query.save(item_id, firewall_id, s_ip, protocol, d_ip, d_port):
            flash(u'保存入口规则 %s =%s=> %s:%s 成功' % (s_ip, protocol, d_ip, d_port))
        else:
            flash(u'入口规则 %s =%s=> %s:%s 已存在' % (s_ip, protocol, d_ip, d_port))
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.index', host_id=host_id))


@ff_in_page.route('/delete/<int:host_id>/<int:item_id>')
def delete(host_id, item_id):
    if InputRule.query.delete(item_id):
        flash(u'删除入口规则 %s 成功' % item_id)
    else:
        flash(u'入口规则 %s 不存在' % item_id)
    return redirect(url_for('.index', host_id=host_id))


@ff_in_page.route('/<int:host_id>/<int:item_id>')
@ff_in_page.route('/<int:host_id>')
@templated('firewall/input.html')
def index(host_id, item_id=None):
    allow_lan = False
    form = None
    h = Host.query.get(host_id)
    rules = []
    if h:
        if h.allow_firewall:
            if h.firewall:
                allow_lan = True
                rules = h.firewall.inputs
                form = InputForm()
                form.host_id.data = h.id
                form.d_ip.choices = [(ip, ip) for ip in h.firewall.wan_ip_list()]
                if item_id:
                    item = InputRule.query.get(item_id)
                    if item:
                        form.item_id.data = item.id
                        form.d_ip.data = item.d_ip
                        form.s_ip.data = item.s_ip
                        form.protocol.data = item.protocol
                        form.d_port.data = item.d_port
                    else:
                        flash(u'入口规则 %s 不存在' % item_id)
            else:
                flash(u'防火墙尚未初始化')
        else:
            flash(u'未启用防火墙')
    else:
        flash(u'主机 %s 不存在' % host_id)
    return dict(host_id=host_id, allow_lan=allow_lan, form=form, rules=rules)


@ff_in_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()