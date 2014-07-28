#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for, flash

from webadm.models.host import Host
from webadm.models.firewall import  NatRule
from webadm.forms.firewall import NatForm, NatBatchForm
from webadm.helpers import templated
from webadm.forms import ip_of_lan_choices

ff_nat_page = Blueprint('ff_nat_page', __name__)

@ff_nat_page.route('/batch_save', methods=['POST'])
def batch_save():
    form = NatBatchForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.s_ip.choices = [(ip, ip) for ip in h.firewall.wan_ip_list()]
    form.d_ip.choices = ip_of_lan_choices(h.firewall.lan_net)
    start_port = int(form.start_port.data)
    stop_port = int(form.end_port.data)

    if form.validate():
        if stop_port > start_port:
            firewall_id = h.firewall_id
            s_ip = form.s_ip.data
            protocol = form.protocol.data
            d_ip = form.d_ip.data
            if NatRule.query.batch_save(firewall_id, s_ip, start_port, stop_port, protocol, d_ip):
                flash(u'批量保存成功')
            else:
                flash(u'规则冲突，可能范围内端口已存在')
        else:
            flash(u'截止端口必须大于起始端口')

    for f in form:
        for error in f.errors:
            flash(error)
    return redirect(url_for('.index', host_id=host_id))

@ff_nat_page.route('/save', methods=['POST'])
def save():
    form = NatForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.s_ip.choices = [(ip, ip) for ip in h.firewall.wan_ip_list()]
    form.d_ip.choices = ip_of_lan_choices(h.firewall.lan_net)

    if form.validate():
        firewall_id = h.firewall_id
        s_ip = form.s_ip.data
        s_port = form.s_port.data
        protocol = form.protocol.data
        d_ip = form.d_ip.data
        d_port = form.d_port.data
        item_id = form.item_id.data

        if NatRule.query.save(item_id, firewall_id, s_ip, s_port, protocol, d_ip, d_port):
            flash(u'保存映射规则 %s:%s =%s=> %s:%s 成功' % (s_ip, s_port, protocol, d_ip, d_port))
        else:
            flash(u'映射规则 %s:%s =%s=>  已存在' % (s_ip, s_port, protocol))

    for f in form:
        for error in f.errors:
            flash(error)
    return redirect(url_for('.index', host_id=host_id))


@ff_nat_page.route('/delete/<int:host_id>/<int:item_id>')
def delete(host_id, item_id):
    if NatRule.query.delete(item_id):
        flash(u'删除映射规则 %s 成功' % item_id)
    else:
        flash(u'映射规则 %s 不存在' % item_id)
    return redirect(url_for('.index', host_id=host_id))


@ff_nat_page.route('/<int:host_id>/<int:item_id>')
@ff_nat_page.route('/<int:host_id>')
@templated('firewall/nat.html')
def index(host_id, item_id=None):
    allow_lan = False
    form = None
    batch_form = None
    h = Host.query.get(host_id)
    rules = []
    if h:
        if h.allow_firewall:
            if h.firewall:
                rules = h.firewall.nats
                form = NatForm()
                batch_form = NatBatchForm()

                form.host_id.data = h.id
                form.s_ip.choices = [(ip, ip) for ip in h.firewall.wan_ip_list()]

                batch_form.host_id.data = h.id
                batch_form.s_ip.choices = [(ip, ip) for ip in h.firewall.wan_ip_list()]
                batch_form.d_ip.choices = ip_of_lan_choices(h.firewall.lan_net)

                if h.firewall.lan_net:
                    allow_lan = True
                    form.d_ip.choices = ip_of_lan_choices(h.firewall.lan_net)
                    if item_id:
                        item = NatRule.query.get(item_id)
                        if item:
                            form.item_id.data = item.id
                            form.s_ip.data = item.s_ip
                            form.s_port.data = item.s_port
                            form.protocol.data = item.protocol
                            form.d_ip.data = item.d_ip
                            form.d_port.data = item.d_port
                        else:
                            flash(u'映射规则 %s 不存在' % item_id)
                else:
                    flash(u'局域网尚未启用路由功能')
            else:
                flash(u'防火墙尚未初始化')
        else:
            flash(u'未启用防火墙')
    else:
        flash(u'主机 %s 不存在' % host_id)
    return dict(host_id=host_id, allow_lan=allow_lan, form=form, batch_form=batch_form,rules=rules)


@ff_nat_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()

  