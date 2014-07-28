#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, flash, url_for, redirect, request
from webadm.models.host import Host
from webadm.models.named import Named, Zone, NSItem
from webadm.helpers import templated
from webadm.forms.named import BasicForm, SetupForm, ZoneForm, AForm, MXForm
from webadm.views import ButtonForm
from webadm.utils.rpc import Rpc

named_page = Blueprint('named_page', __name__)


@named_page.route('/')
@templated('named/index.html')
def index():
    return dict(act='item')


@named_page.route('/a', methods=['POST'])
def on_a():
    form = AForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.zone_id.choices = [(z.id, z) for z in h.named.zones]
    if form.validate():
        NSItem.query.save(form.zone_id.data, form.prefix.data, form.target.data)
        flash(u'保存A记录成功')
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.zone', host_id=host_id))


@named_page.route('/mx', methods=['POST'])
def on_mx():
    form = MXForm(request.form)
    host_id = form.host_id.data
    form.ns_id.choices = NSItem.query.ns_choices(host_id)

    if form.validate():
        NSItem.query.set_mx(form.ns_id.data, form.priority.data)
        flash(u'保存MX记录成功')
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.zone', host_id=host_id))


@named_page.route('/zone', methods=['POST'])
def on_zone():
    form = ZoneForm(request.form)
    host_id = form.host_id.data
    if form.validate():
        domain = form.domain.data
        flash(
            (Zone.query.save(form.named_id.data, form.zone_id.data, domain) and u'保存域名 %s 成功' or u'域名 %s 已存在') % domain)
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.zone', host_id=host_id))


@named_page.route('/setup', methods=['POST'])
def on_setup():
    form = SetupForm(request.form)
    host_id = form.host_id.data
    if form.validate():
        if Named.query.set(form.named_id.data, form.listen_on_ips.data, form.forwarders.data, form.controls.data):
            flash(u'保存网络参数成功')
        else:
            flash(u'保存网络参数失败')
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.item', host_id=host_id))


@named_page.route('/basic', methods=['POST'])
def on_basic():
    form = BasicForm(request.form)
    host_id = form.host_id.data
    if form.validate():
        if Named.query.save(host_id, form.caption.data, form.details.data):
            flash(u'保存基本成功')
        else:
            flash(u'保存基本信息失败')
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.item', host_id=host_id))

@named_page.route('/ns_del/<int:host_id>/<int:ns_id>')
def ns_delete(host_id, ns_id):
    flash((NSItem.query.delete(ns_id) and u'删除域名解析记录 %s 成功' or u'删除域名解析记录 %s 失败') % ns_id)
    return redirect(url_for('.item', host_id=host_id))

@named_page.route('/zone_del/<int:host_id>/<int:zone_id>')
def zone_delete(host_id, zone_id):
    flash((Zone.query.delete(zone_id) and u'删除域名记录 %s 成功' or u'删除域名记录 %s 失败') % zone_id)
    return redirect(url_for('.item', host_id=host_id))

@named_page.route('/upload', methods=['POST'])
def upload():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_named and h.named:
        try:
            for msg in rpc.named_save(h.named.listen_on_ips, h.named.forwarders, h.named.controls, h.named.zones):
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DNS服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))

@named_page.route('/status', methods=['POST'])
def status():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_named and h.named:
        try:
            for msg in rpc.named_status():
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DNS服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))
@named_page.route('/start', methods=['POST'])
def start():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_named and h.named:
        try:
            for msg in rpc.named_start():
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DNS服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))

@named_page.route('/stop', methods=['POST'])
def stop():
    host_id = request.form['id']
    h = Host.query.get(host_id)
    rpc = Rpc(h.listen_ip)
    if h.allow_named and h.named:
        try:
            for msg in rpc.named_stop():
                flash(msg)
        except Exception, e:
            flash(e.message)
    else:
        flash(u'主机 %s 尚未设置DNS服务' % h)
    return redirect(url_for('.mgr', host_id=host_id))



@named_page.route('/zone/<int:host_id>/<int:zone_id>')
@named_page.route('/zone/<int:host_id>')
@templated('named/zone.html')
def zone(host_id, zone_id=None):
    h = Host.query.get(host_id)
    fm_zone = None
    fm_a = None
    fm_mx = None
    zones = []
    allow=False

    if h:
        if h.allow_named:
            if h.named:
                if h.named.listen_on_ips:
                    allow = True

                    fm_zone = ZoneForm()
                    fm_zone.host_id.data = h.id
                    fm_zone.named_id.data = h.named.id

                    fm_a = AForm()
                    fm_a.host_id.data = h.id
                    fm_a.zone_id.choices = [(i.id, i) for i in h.named.zones]
                    fm_mx = MXForm()
                    fm_mx.host_id.data = h.id
                    fm_mx.ns_id.choices = NSItem.query.ns_choices(host_id)

                    if zone_id:
                        z = Zone.query.get(zone_id)
                        if z:
                            fm_zone.zone_id.data = z.id
                            fm_zone.domain.data = z.domain
                            fm_a.zone_id.data = zone_id
                        else:
                            flash(u'域名 %s 不存在')

                    zones = h.named.zones
                else:
                    flash(u'主机 %s 的DNS服务尚未设置网络' % h)
            else:
                flash(u'主机 %s 尚未初始化DNS服务' % h)
        else:
            flash(u'主机 %s 尚未允许DNS服务' % h)
    else:
        flash(u'主机 %s 不存在' % host_id)

    return dict(host_id=host_id, fm_zone=fm_zone, fm_mx=fm_mx, fm_a=fm_a, zones=zones, allow=allow)

@named_page.route('/mgr/<int:host_id>')
@templated('named/mgr.html')
def mgr(host_id):
    h = Host.query.get(host_id)
    forms = None
    if h:
        if h.allow_named:
            if h.named:
                if h.named.listen_on_ips:
                    forms = [
                        ButtonForm(u'当前状态', url_for('.status'), host_id),
                        ButtonForm(u'保存配置', url_for('.upload'), host_id),
                        ButtonForm(u'启动服务', url_for('.start'), host_id),
                        ButtonForm(u'停止服务', url_for('.stop'), host_id),
                    ]
                else:
                    flash(u'主机 %s 的DNS服务尚未设置网络' % h)
            else:
                flash(u'主机 %s 尚未初始化DNS服务' % h)
        else:
            flash(u'主机 %s 尚未允许DNS服务' % h)
    else:
        flash(u'主机 %s 不存在' % host_id)

    return dict(host_id=host_id, forms=forms)

@named_page.route('/<int:host_id>')
@templated('named/item.html')
def item(host_id):
    fm_basic = None
    fm_setup = None
    h = Host.query.get(host_id)

    if h:
        if h.allow_named:
            fm_basic = BasicForm()
            fm_basic.host_id.data = h.id
            if h.named:
                fm_basic.caption.data = h.named.caption
                fm_basic.details.data = h.named.details

                fm_setup = SetupForm()
                fm_setup.host_id.data = h.id
                fm_setup.named_id.data = h.named.id
                fm_setup.forwarders.data = h.named.forwarders
                fm_setup.listen_on_ips.data = h.named.listen_on_ips
                fm_setup.controls.data = h.named.controls
        else:
            flash(u'主机 %s 尚未允许DNS服务' % h)
    else:
        flash(u'主机 %s 不存在' % host_id)

    return dict(fm_basic=fm_basic, fm_setup=fm_setup,
                host_id=host_id)


@named_page.route('/act', methods=['POST'])
def action():
    return redirect(url_for('named_page.%s' % request.form['act'], host_id=request.form['id']))

@named_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_named == True).all()