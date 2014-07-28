#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, g, request, redirect, url_for
from webadm.helpers import templated
from webadm.models.host import Host

firewall_page = Blueprint('firewall_page', __name__)

@firewall_page.route('/')
@templated('firewall/index.html')
def index():
    return {'act': 'item'}


@firewall_page.route('/act', methods=['POST'])
def action():
    return redirect(url_for('ff_%s_page.index' % request.form['act'], host_id=request.form['id']))


@firewall_page.before_request
def before_request():
    g.hosts = Host.query.filter(Host.allow_firewall == True).all()


  