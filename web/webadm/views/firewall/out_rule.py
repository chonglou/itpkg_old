#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for, flash

from webadm.models.host import Host, MacInfo
from webadm.models.firewall import  OutputRule
from webadm.forms.firewall import OutputElseForm, OutputForm
from webadm.helpers import templated

ff_out_page = Blueprint('ff_out_page', __name__)


@ff_out_page.route('/else/<int:host_id>/<int:item_id>/<int:mac_id>')
def else_delete(host_id, item_id, mac_id):
    if OutputRule.query.delete_else(item_id, mac_id):
        flash(u'删除例外规则成功')
    else:
        flash(u'操作失败，例外规则不存在')
    return redirect(url_for('.index', host_id=host_id))


@ff_out_page.route('/else', methods=['POST'])
def else_save():
    form = OutputElseForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)
    form.mac.choices = [(m.id, m) for m in h.macs]
    form.output.choices = [(o.id, o) for o in h.firewall.outputs]
    if form.validate():
        if OutputRule.query.set_else(form.output.data, form.mac.data):
            flash(u'保存例外规则成功')
        else:
            flash(u'操作失败，例外规则已存在')
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.index', host_id=host_id))


@ff_out_page.route('/', methods=['POST'])
def save():
    form = OutputForm(request.form)
    host_id = form.host_id.data
    h = Host.query.get(host_id)

    if form.validate():
        firewall_id = h.firewall_id
        item_id = form.item_id.data
        domain = form.domain.data
        mon = form.mon.data
        tue = form.tue.data
        wed = form.wed.data
        thu = form.thu.data
        fri = form.fri.data
        sat = form.sat.data
        sun = form.sun.data
        start = form.start.data
        end = form.end.data

        check = True
        if start == end:
            flash(u'起始时间和截止时间相同')
            check = False
        if not mon and not tue and not wed and not thu and not fri and not sat and not sun:
            flash(u'请选择生效日')
            check = False

        if check:
            if OutputRule.query.save(item_id, firewall_id, domain, start, end, mon=mon,
                tue=tue, wed=wed, thu=thu, fri=fri, sat=sat, sun=sun):
                flash(u'保存出口规则 %s［%s, %s］成功' % (domain, start, end))
            else:
                flash(u'出口规则 %s［%s, %s］ 已存在' % (domain, start, end))
    else:
        for f in form:
            for error in f.errors:
                flash(error)
    return redirect(url_for('.index', host_id=host_id))


@ff_out_page.route('/delete/<int:host_id>/<int:item_id>')
def delete(host_id, item_id):
    if OutputRule.query.delete(item_id):
        flash(u'删除出口规则 %s 成功' % item_id)
    else:
        flash(u'出口规则 %s 不存在' % item_id)
    return redirect(url_for('.index', host_id=host_id))


@ff_out_page.route('/<int:host_id>/<int:item_id>')
@ff_out_page.route('/<int:host_id>')
@templated('firewall/output.html')
def index(host_id, item_id=None):
    allow_lan = False
    form = None
    fm_else = None
    h = Host.query.get(host_id)
    rules = []
    if h:
        if h.allow_firewall:
            if h.firewall:
                allow_lan = True
                rules = h.firewall.outputs

                form = OutputForm()
                fm_else = OutputElseForm()

                form.host_id.data = h.id
                fm_else.host_id.data = h.id
                fm_else.mac.choices = [(
                    m.id, m) for m in MacInfo.query.filter(MacInfo.host_id == host_id).order_by(MacInfo.ip)]
                fm_else.output.choices = [(o.id, o) for o in h.firewall.outputs]

                if item_id:
                    item = OutputRule.query.get(item_id)
                    if item:
                        form.domain.data = item.domain
                        form.item_id.data = item.id
                        form.start.data = item.start
                        form.end.data = item.end
                        form.mon.data = item.mon
                        form.tue.data = item.tue
                        form.wed.data = item.wed
                        form.thu.data = item.thu
                        form.fri.data = item.fri
                        form.sat.data = item.sat
                        form.sun.data = item.sun

                    else:
                        flash(u'出口规则 %s 不存在' % item_id)
            else:
                flash(u'防火墙尚未初始化')
        else:
            flash(u'未启用防火墙')
    else:
        flash(u'主机 %s 不存在' % host_id)
    return dict(host_id=host_id, allow_lan=allow_lan, fm_out=form, fm_else=fm_else, rules=rules)


@ff_out_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()