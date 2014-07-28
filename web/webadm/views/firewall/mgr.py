#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, flash, url_for, session
from webadm.models.host import Host, MacInfo
from webadm.models.firewall import LimitRule
from webadm.views import ButtonForm
from webadm.helpers import templated
from webadm.utils.rpc import Rpc
from webadm.utils import ArpItem


ff_mgr_page = Blueprint('ff_mgr_page', __name__)

@ff_mgr_page.route('/status_mac', methods=['POST'])
@templated('firewall/status.html')
def status_mac():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    macs=[]
    rpc = Rpc(h.listen_ip)
    try:
        i = 0
        for line in rpc.arp_scan():
            if i:
                macs.append(ArpItem(line))
            i +=1
    except Exception, e:
        flash(e.message)
    return macs and dict(next=url_for('.index', host_id=host_id), macs=macs, host_id=host_id) or redirect(url_for('.index', host_id=host_id))


@ff_mgr_page.route('/status_ff', methods=['POST'])
@templated('firewall/status.html')
def status_ff():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    ffs = []
    try:
        ffs = rpc.ff_status()
    except Exception, e:
        flash(e.message)
    return ffs and dict(next=url_for('.index', host_id=host_id), msgs=ffs, host_id=host_id) or redirect(url_for('.index', host_id=host_id))

@ff_mgr_page.route('/status_tc', methods=['POST'])
@templated('firewall/status.html')
def status_tc():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    tcs = []
    if h.firewall.allow_lan and h.firewall.lan_device:
        try:
            tcs=rpc.tc_status(h.firewall.wan_device, h.firewall.lan_device)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未启用局域网或未设置相关设备' % h)
    return tcs and dict(next=url_for('.index', host_id=host_id), msgs=tcs, host_id=host_id) or redirect(url_for('.index', host_id=host_id))

@ff_mgr_page.route('/apply_ff', methods=['POST'])
@templated('firewall/status.html')
def apply_ff():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    msgs=[]
    wan_device = h.firewall.wan_device
    allow_ping = h.firewall.allow_ping
    lan_device = None
    lan_net=None
    ins=h.firewall.inputs
    outs=[]
    macs=[]
    nats=[]

    rpc = Rpc(h.listen_ip)
    if h.firewall and h.allow_firewall:
        if h.firewall.allow_lan:
            lan_device = h.firewall.lan_device
            lan_net  = h.firewall.lan_net
            outs = h.firewall.outputs
            nats = h.firewall.nats
            macs =  MacInfo.query.filter(MacInfo.host_id==host_id).filter(MacInfo.allow==True).all()
        try:
            msgs = rpc.ff_apply(wan_device, allow_ping, lan_device, lan_net, ins=ins, outs=outs, macs=macs, nats=nats)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'防火墙尚未启用')
    return msgs and dict(next=url_for('.index', host_id=host_id), msgs=msgs, host_id=host_id) or redirect(url_for('.index', host_id=host_id))

@ff_mgr_page.route('/apply_tc', methods=['POST'])
@templated('firewall/status.html')
def apply_tc():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    msgs=[]
    if h.firewall and h.allow_firewall and h.firewall.allow_lan:
        def_limit = None
        if h.firewall.limit_id:
            def_limit = LimitRule.query.get(h.firewall.limit_id)
        if def_limit:
            try:
                msgs = rpc.tc_apply(h.firewall.wan_device, h.firewall.lan_device, h.firewall.lan_net, h.macs, def_limit)
            except Exception, e:
                flash(e.message)
        else:
            flash(u'未设置默认限速规则')
    else:
        flash(u'防火墙未启用或未允许局域网')
    return msgs and dict(next=url_for('.index', host_id=host_id), msgs=msgs, host_id=host_id) or redirect(url_for('.index', host_id=host_id))

@ff_mgr_page.route('/clear_ff', methods=['POST'])
@templated('firewall/status.html')
def clear_ff():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    msgs=[]
    if h.firewall.allow_lan and h.firewall.lan_device:
        try:
            msgs = rpc.ff_clear(h.firewall.wan_device, h.firewall.lan_net)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未启用局域网或未设置相关设备' % h)
    return msgs and dict(next=url_for('.index', host_id=host_id), msgs=msgs, host_id=host_id) or redirect(url_for('.index', host_id=host_id))


@ff_mgr_page.route('/clear_tc', methods=['POST'])
@templated('firewall/status.html')
def clear_tc():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    msgs = []
    if h.firewall.allow_lan and h.firewall.lan_device:
        try:
            msgs= rpc.tc_clear(h.firewall.wan_device, h.firewall.lan_device)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未启用局域网或未设置相关设备' % h)
    return msgs and dict(next=url_for('.index', host_id=host_id), msgs=msgs, host_id=host_id) or redirect(url_for('.index', host_id=host_id))

@ff_mgr_page.route(('/<int:host_id>'))
@templated('/firewall/mgr.html')
def index(host_id):
    h = Host.query.get(host_id)
    allow = False
    forms = []

    if h:
        if h.allow_firewall:
            if h.firewall:
                if h.firewall.wan_device:
                    allow = True
                    forms = [
                        ButtonForm(u'当前MAC状态', url_for('.status_mac'), host_id),
                        ButtonForm(u'当前防火墙状态', url_for('.status_ff'), host_id),
                        ButtonForm(u'当前限速状态', url_for('.status_tc'), host_id),
                        ButtonForm(u'应用防火墙规则', url_for('.apply_ff'), host_id),
                        ButtonForm(u'应用限速规则', url_for('.apply_tc'), host_id),
                        ButtonForm(u'清空防火墙规则', url_for('.clear_ff'), host_id),
                        ButtonForm(u'清空限速规则', url_for('.clear_tc'), host_id),
                    ]
                else:
                    flash(u'主机 %s 的防火墙尚未设置WAN' % h)
            else:
                flash(u'主机 %s 的防火墙尚未初始化' % h)
        else:
            flash(u'主机 %s 防火墙未启用' % h)
    else:
        flash(u'主机 %s 不存在')


    return dict(host_id=host_id, allow=allow, forms=forms, timeout=Rpc.TIMEOUT)


@ff_mgr_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()