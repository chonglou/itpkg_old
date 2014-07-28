#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'


#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, flash, url_for

from webadm.models.host import Host
from webadm.forms.firewall import BasicForm
from webadm.helpers import templated
from webadm.models.firewall import Firewall

ff_item_page = Blueprint('ff_item_page', __name__)

@ff_item_page.route('/<int:host_id>', methods=['POST', 'GET'])
@templated('/firewall/item.html')
def index(host_id):
    h = Host.query.get(host_id)
    form = BasicForm(request.form)
    allow = False

    if request.method == 'POST':
        if form.validate():
            if  Firewall.query.save(host_id, form.caption.data, form.details.data):
                flash(u'保存防火墙 %s 基本信息成功' % host_id)
            else:
                flash(u'主机 %s 不支持没有启用防火墙或是不存在' % host_id)

    if h:
        if h.allow_firewall:
            allow = True
            if h.firewall:
                form.caption.data = h.firewall.caption
                form.details.data = h.firewall.details
        else:
            flash(u'主机 %s 防火墙未启用' % h)
    else:
        flash(u'主机 %s 不存在')

    return dict(form=form, host_id=host_id, allow=allow)


@ff_item_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()
