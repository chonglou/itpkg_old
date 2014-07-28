#!/usr/bin/env python
#coding=utf-8
from webadm.helpers import templated

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, flash, redirect, url_for
from webadm.models.host import Host
from webadm.forms.host import HostForm

host_page = Blueprint('host_page', __name__)

@host_page.route('/')
def index():
    return redirect(url_for('host_page.list'))


@host_page.route('/delete/<int:host_id>')
def delete(host_id):
    if Host.query.delete(host_id):
        flash(u'删除主机 %s 成功' % host_id)
    else:
        flash(u'主机 %s 不存在' % host_id)
    return redirect(url_for('.list'))


@host_page.route('/edit', methods=['GET', 'POST'])
@host_page.route('/edit/<int:host_id>')
@templated('host/edit.html')
def edit(host_id=None):
    form = HostForm(request.form)
    if request.method == 'POST':
        if form.validate():
            id = form.host_id.data
            caption = form.caption.data
            details = form.details.data
            listen_ip = form.listen_ip.data
            allow_dhcpd4 = form.allow_dhcpd4.data
            allow_named = form.allow_named.data
            allow_firewall = form.allow_firewall.data
            allow_mail = form.allow_mail.data
            allow_vpn = form.allow_vpn.data
            manager_ip = form.manager_ip.data

            if Host.query.save(id, caption, details, allow_dhcpd4=allow_dhcpd4,
                               allow_mail=allow_mail, allow_named=allow_named, allow_vpn=allow_vpn,
                               allow_firewall=allow_firewall, listen_ip=listen_ip, manager_ip=manager_ip):
                flash(u'保存主机成功 ')
                form = HostForm()
            else:
                flash(u'主机 %s 不存在' % id)

    if host_id:
        h = Host.query.get(host_id)
        if h:
            form.host_id.data = h.id
            form.caption.data = h.caption
            form.details.data = h.details
            form.allow_dhcpd4.data = h.allow_dhcpd4
            form.allow_named.data = h.allow_named
            form.allow_firewall.data = h.allow_firewall
            form.allow_mail.data = h.allow_mail
            form.allow_vpn.data = h.allow_vpn
            form.listen_ip.data = h.listen_ip
            form.manager_ip.data = h.manager_ip
        else:
            flash(u'主机 %s 不存在' % host_id)
    return dict(form=form)


@host_page.route('/list')
@templated('host/list.html')
def list():
    return dict()


@host_page.before_request
def before_request():
    g.hosts = Host.query.all()