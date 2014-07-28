#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for, flash

from webadm.helpers import templated
from webadm.models.host import Host, MacInfo
from webadm.models.dhcpd import Dhcpd4
from webadm.forms.dhcpd import Dhcpd4BasicForm, Dhcpd4SetupForm, MacIpBindForm
from webadm.forms import ip_of_lan_choices
from webadm.views import ButtonForm
from webadm.utils.rpc import Rpc

dhcpd_page = Blueprint('dhcpd_page', __name__)

@dhcpd_page.route('/')
@templated('dhcpd/index.html')
def index():
    return dict(act='item')


@dhcpd_page.route('/bind', methods=['POST'])
def bind():
    form = MacIpBindForm(request.form)
    mac = form.mac_id.data
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.mac_id.choices = [(m.id, m) for m in h.macs]
    form.ip.choices = ip_of_lan_choices(h.dhcpd4.lan_net)
    if form.validate():
        ip = form.ip.data
        bind = form.bind.data
        if MacInfo.query.bind(mac, ip, bind):
            if bind:
                flash(u'绑定 %s 成功' % ip)
            else:
                flash(u'解除绑定  %s 成功' % ip)
        else:
            flash(u'IP %s 已被绑定' % ip)

    for f in form:
        for error in f.errors:
            flash(error)
    return redirect(url_for('.mac', host_id=host_id))


@dhcpd_page.route('/save', methods=['POST'])
@templated('msg.html')
def save():
    form = Dhcpd4BasicForm(request.form)
    host_id = form.host_id.data
    if form.validate():
        if Dhcpd4.query.save(host_id, form.caption.data, form.details.data):
            flash(u'保存DNS服务器 %s 基本信息成功' % host_id)
        else:
            flash(u'主机 %s 不支持DHCPD服务或是不存在' % host_id)
        return redirect(url_for('.item', host_id=host_id))
    msgs = []
    for f in form:
        for error in f.errors:
            msgs.append(error)
    return dict(page='dhcpd', msgs=msgs, next=url_for('.item', host_id=host_id))


@dhcpd_page.route('/setup', methods=['POST'])
@templated('msg.html')
def setup():
    form = Dhcpd4SetupForm(request.form)
    host_id = form.host_id.data
    if form.validate():
        if Dhcpd4.query.setup(host_id, form.domain.data, form.lan_net.data, form.dns_1.data, form.dns_2.data):
            flash(u'保存DNS服务器 %s 网络参数成功' % host_id)
        else:
            flash(u'主机 %s 不支持DHCPD服务或是不存在，请先设定基本信息' % host_id)
        return redirect(url_for('.item', host_id=host_id))
    msgs = []
    for f in form:
        for error in f.errors:
            msgs.append(error)
    return dict(page='dhcpd', msgs=msgs, next=url_for('.item', host_id=host_id))


@dhcpd_page.route('/upload', methods=['POST'])
def upload():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_dhcpd4:
        try:
            for msg in rpc.dhcpd_save(h.dhcpd4.domain, h.dhcpd4.lan_net, h.dhcpd4.dns_1, h.dhcpd4.dns_2, h.macs):
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DHCP服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))


@dhcpd_page.route('/start', methods=['POST'])
def start():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_dhcpd4:
        try:
            for msg in rpc.dhcpd_start():
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DHCP服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))


@dhcpd_page.route('/stop', methods=['POST'])
def stop():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_dhcpd4:
        try:
            for msg in rpc.dhcpd_stop():
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DHCP服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))


@dhcpd_page.route('/status', methods=['POST'])
def status():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_dhcpd4:
        try:
            for msg in rpc.dhcpd_status():
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DHCP服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))

@dhcpd_page.route('/item/<int:host_id>')
@templated('dhcpd/item.html')
def item(host_id):
    base_form = None
    setup_form = None

    h = Host.query.get(host_id)
    if h:
        if h.allow_dhcpd4:
            base_form = Dhcpd4BasicForm()
            base_form.host_id.data = h.id
            if h.dhcpd4:
                base_form.caption.data = h.dhcpd4.caption
                base_form.details.data = h.dhcpd4.details
                if h.dhcpd4.caption:
                    setup_form = Dhcpd4SetupForm()
                    setup_form.host_id.data = h.id
                    setup_form.dns_1.data = h.dhcpd4.dns_1 or '192.168.0.1'
                    setup_form.dns_2.data = h.dhcpd4.dns_2 or '192.168.0.1'
                    setup_form.domain.data = h.dhcpd4.domain or 'lzl98.com'
                    setup_form.lan_net.data = h.dhcpd4.lan_net or '192.168.0.0'
        else:
            flash(u'主机 %s 尚未启用DHCPD服务' % host_id)
    else:
        flash(u'主机 %s 不存在' % host_id)
    return {'base_form': base_form, 'setup_form': setup_form, 'host_id': host_id}
@dhcpd_page.route('/mac/<int:host_id>/<int:mac_id>')
@dhcpd_page.route('/mac/<int:host_id>')
@templated('dhcpd/mac.html')
def mac(host_id, mac_id=None):
    allow = False
    h = Host.query.get(host_id)
    bind_form = None
    macs = []

    if h:
        if h.allow_dhcpd4:
            if h.dhcpd4:
                if h.dhcpd4.lan_net:
                    allow=True
                    
                    for m in h.macs:
                        if m.bind:
                            macs.append(m)

                    bind_form = MacIpBindForm()
                    bind_form.host_id.data = h.id
                    bind_form.mac_id.choices = [(m.id, m) for m in MacInfo.query.filter(MacInfo.host_id==host_id).order_by(MacInfo.ip).all()]
                    bind_form.ip.choices = ip_of_lan_choices(h.dhcpd4.lan_net)
                    if mac_id:
                        mac = MacInfo.query.get(mac_id)
                        if mac:
                            bind_form.mac_id.data = mac.id
                            bind_form.ip.data = mac.ip
                            bind_form.bind.data = mac.bind
                        else:
                            flash(u'MAC记录 %s 不存在' % mac_id)
                else:
                    flash(u'主机 %s 的DHCP服务尚未设置网络信息' % h)
            else:
                flash(u'主机 %s 的DHCP服务尚未初始化' % h)
        else:
            flash(u'主机 %s 尚未启用DHCP服务' %h)
    else:
        flash(u'主机 %s 不存在' % host_id)
    return dict(allow=allow, bind_form=bind_form, host_id=host_id, macs=macs)

@dhcpd_page.route('/mgr/<int:host_id>')
@templated('dhcpd/mgr.html')
def mgr(host_id):
    allow = False
    h = Host.query.get(host_id)
    forms=None
    
    if h:
        if h.allow_dhcpd4:
            if h.dhcpd4:
                if h.dhcpd4.lan_net:
                    allow=True
                    forms = [
                        ButtonForm(u'当前状态', url_for('.status'), id=host_id),
                        ButtonForm(u'保存配置', url_for('.upload'), id=host_id),
                        ButtonForm(u'启动服务', url_for('.start'), id=host_id),
                        ButtonForm(u'停止服务', url_for('.stop'), id=host_id),
                        ]
                else:
                    flash(u'主机 %s 的DHCP服务尚未设置网络信息' % h)
            else:
                flash(u'主机 %s 的DHCP服务尚未初始化' % h)
        else:
            flash(u'主机 %s 尚未启用DHCP服务' %h)
    else:
        flash(u'主机 %s 不存在' % host_id)
    return dict(allow=allow, forms=forms, host_id=host_id)

@dhcpd_page.route('/act', methods=['POST'])
def action():
    return redirect(url_for('dhcpd_page.%s' % request.form['act'], host_id=request.form['id']))

@dhcpd_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_dhcpd4 == True).all()
  