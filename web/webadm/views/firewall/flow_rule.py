#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, redirect, flash, url_for, request

from webadm.helpers import templated
from webadm.models.host import Host, MacInfo
from webadm.models.firewall import LimitRule
from webadm.forms.flow import LimitForm, MacLimitForm, DefLimitForm

ff_flow_page = Blueprint('ff_flow_page', __name__)


@ff_flow_page.route('/save', methods=['POST'])
def save():
    form = LimitForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)

    if form.validate():
        firewall_id = h.firewall_id
        caption = form.caption.data
        up_rate = form.up_rate_speed.data
        down_rate = form.down_rate_speed.data
        up_ceil = form.up_ceil_speed.data
        down_ceil = form.down_ceil_speed.data
        item_id = form.item_id.data
        if up_rate < up_ceil and down_rate < down_ceil:
            if LimitRule.query.save(item_id, firewall_id, caption, up_rate, down_rate, up_ceil, down_ceil):
                flash(u'保存限速规则 ［(%s,%s) (%s,%s)］ 成功' % (up_rate, down_rate, up_ceil, down_ceil))
            else:
                flash(u'限速规则 %s 已存在' % caption)
        else:
            flash(u'最大带宽应该大于保证带宽')
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.index', host_id=host_id))


@ff_flow_page.route('/link/<int:host_id>/<int:item_id>,<int:mac_id>/delete')
def delete_link(host_id, item_id, mac_id):
    flash(LimitRule.query.delete_link(item_id, mac_id) and u'删除关联成功' or u'操作失败')
    return redirect(url_for('.index', host_id=host_id))


@ff_flow_page.route('/delete/<int:host_id>/<int:item_id>')
def delete(host_id, item_id):
    if LimitRule.query.delete(item_id):
        flash(u'删除映射规则 %s 成功' % item_id)
    else:
        flash(u'映射规则 %s 不存在' % item_id)
    return redirect(url_for('.index', host_id=host_id))


@ff_flow_page.route('/set_def', methods=['POST'])
def set_def():
    form = DefLimitForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.limit_id.choices = [(i.id, i)for i in h.firewall.limits]
    from webadm.models.firewall import Firewall

    flash(Firewall.query.set_default_limit(h.firewall.id, form.limit_id.data) and u'设置默认规则成功' or u'设置默认规则失败')
    return redirect(url_for('.index', host_id=host_id))


@ff_flow_page.route('/link', methods=['POST'])
def link():
    form = MacLimitForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.limit_id.choices = [(i.id, i)for i in h.firewall.limits]
    form.mac_id.choices = [(i.id, i) for i in h.macs]
    mac_id = form.mac_id.data
    limit_id = form.limit_id.data
    MacInfo.query.set_limit(mac_id, limit_id)
    flash(' %s ==> %s' % (limit_id, mac_id))
    flash(u'保存成功')
    return redirect(url_for('.index', host_id=host_id))


@ff_flow_page.route('/<int:host_id>/<int:item_id>')
@ff_flow_page.route('/<int:host_id>')
@templated('firewall/flow.html')
def index(host_id, item_id=None):
    allow = False
    fm_limit = None
    fm_link = None
    fm_def = None
    def_limit_id = None

    h = Host.query.get(host_id)
    rules = []
    if h:
        if h.allow_firewall:
            if h.firewall:
                rules = h.firewall.limits
                if h.firewall.allow_lan:
                    if h.firewall.lan_device:
                        allow = True
                        fm_limit = LimitForm()
                        fm_limit.host_id.data = h.id

                        fm_link = MacLimitForm()
                        fm_link.host_id.data = h.id
                        fm_link.limit_id.choices = [(i.id, i)for i in rules]
                        fm_link.mac_id.choices = [(
                            i.id, i)  for i in
                                      MacInfo.query.filter(MacInfo.host_id == host_id).order_by(MacInfo.ip).all()
                        ]

                        fm_def = DefLimitForm()
                        fm_def.host_id.data = h.id
                        fm_def.limit_id.choices = [(i.id, i)for i in rules]

                        def_limit_id = h.firewall.limit_id

                        if item_id:
                            item = LimitRule.query.get(item_id)
                            if item:
                                fm_limit.caption.data = item.caption
                                fm_limit.up_ceil_speed.data = item.up_ceil
                                fm_limit.down_ceil_speed.data = item.down_ceil
                                fm_limit.up_rate_speed.data = item.up_rate
                                fm_limit.down_rate_speed.data = item.down_rate
                                fm_limit.item_id.data = item.id
                            else:
                                flash(u'限速规则 %s 不存在' % item_id)
                    else:
                        flash(u'局域网尚未初始化')
                else:
                    flash(u'局域网尚未启用局域网功能')
            else:
                flash(u'防火墙尚未初始化')
        else:
            flash(u'未启用防火墙')
    else:
        flash(u'主机 %s 不存在' % host_id)
    return dict(host_id=host_id, allow=allow, fm_limit=fm_limit, fm_link=fm_link, fm_def=fm_def,
        rules=rules, def_limit_id=def_limit_id)


@ff_flow_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()

