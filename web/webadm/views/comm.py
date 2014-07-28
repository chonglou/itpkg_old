#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, redirect, url_for, request, flash
from webadm.helpers import templated
from webadm.models.site import Note

comm_page = Blueprint('comm_page', __name__)


def index():
    return redirect(url_for('personal_page.index'))


@templated('search.html')
def search():
    macs=[]
    employees=[]
    key = request.form['key'].strip()
    if key:
        from webadm.models.host import MacInfo
        from webadm.models.employee import Employee
        macs = MacInfo.query.search_by_key(key)
        employees = Employee.query.search_by_key(key)
    else:
        flash(u'请输入关键字')
    return dict(macs=macs, employees=employees)

@templated('about_me.html')
def about_me():
    return dict(notes=Note.query.all_by_created())