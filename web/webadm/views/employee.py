#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Blueprint, redirect, url_for, request, flash
from webadm.helpers import templated
from webadm.forms.employee import UnitForm, EmployeeForm, ContactForm
from webadm.models.employee import Employee, Unit, Contact

employee_page = Blueprint('employee_page', __name__)


@employee_page.route('/')
def index():
    return redirect(url_for('.user'))


@employee_page.route('/contact', methods=['GET', 'POST'])
@employee_page.route('/contact/<int:user_id>')
@templated('employee/contact.html')
def contact(user_id=None):
    form = ContactForm(request.form)

    if request.method == 'POST':
        if form.validate():
            id = form.user_id.data
            if Employee.query.set_contact(id, form.qq.data, form.msn.data, form.email.data, form.phone.data,
                                          form.tel.data, form.fax.data, form.work.data, form.home.data):
                flash(u'保存 %s 的通讯方式成功' % id)
            else:
                flash(u'雇员 %s 不存在' % id)
            return redirect(url_for('.contact'))

    if user_id:
        u = Employee.query.get(user_id)
        if u:
            form.user_id.data = u.id
            if u.contact:
                form.qq.data = u.contact.qq
                form.msn.data = u.contact.msn
                form.email.data = u.contact.email
                form.phone.data = u.contact.phone
                form.tel.data = u.contact.tel
                form.fax.data = u.contact.fax
                form.home.data = u.contact.home
                form.work.data = u.contact.work
        else:
            flash(u'雇员 %s 不存在' % user_id)
    else:
        form = None
    return dict(form=form, contacts=Contact.query.all())


@employee_page.route('/unit/<int:unit_id>/delete')
def unit_delete(unit_id):
    if Unit.query.delete(unit_id):
        flash(u'删除部门 %s 成功' % unit_id)
    else:
        flash(u'部门 %s 不存在' % unit_id)
    return redirect(url_for('.unit'))


@employee_page.route('/user/<int:user_id>/delete')
def user_delete(user_id):
    if Employee.query.delete(user_id):
        flash(u'删除雇员 %s 成功' % user_id)
    else:
        flash(u'雇员 %s 不存在' % user_id)
    return redirect(url_for('.user'))


@employee_page.route('/user', methods=['GET', 'POST'])
@employee_page.route('/user/<int:user_id>')
@templated('employee/user.html')
def user(user_id=None):
    form = EmployeeForm(request.form)
    form.unit_id.choices = [(u.id, u.caption) for u in Unit.query.order_by(Unit.caption).all()]

    if request.method == 'POST':
        if form.validate():
            id = form.user_id.data
            nick_name = form.nick_name.data
            details = form.details.data
            real_name = form.real_name.data
            unit_id = form.unit_id.data

            u = Employee.query.get_by_nick_name(nick_name)
            if id:
                if u and int(id) != u.id:
                    flash(u'昵称 %s 冲突' % nick_name)
                else:
                    if Employee.query.set(id, unit_id, nick_name, real_name, details):
                        flash(u'保存账户 %s 成功' % id)
                    else:
                        flash(u'账户 %s 不存在' % id)
            else:
                if u:
                    flash(u'昵称 %s 冲突' % nick_name)
                else:
                    Employee.query.add(Employee(unit_id, nick_name, real_name, details))
                    flash(u'新增账户 %s 成功' % nick_name)
            return redirect(url_for('.user'))

    contact = None
    if user_id:
        u = Employee.query.get(user_id)
        form.user_id.data = u.id
        form.nick_name.data = u.nick_name
        form.real_name.data = u.real_name
        form.details.data = u.details
        form.unit_id.data = u.unit_id
        contact = u.contact

    return dict(form=form, users=Employee.query.all(), contact=contact)


@employee_page.route('/unit', methods=['GET', 'POST'])
@employee_page.route('/unit/<int:unit_id>')
@templated('employee/unit.html')
def unit(unit_id=None):
    form = UnitForm(request.form)
    if request.method == 'POST':
        if form.validate():
            id = form.unit_id.data
            caption = form.caption.data
            details = form.details.data
            if id:
                if Unit.query.set(id, caption, details):
                    flash(u'保存部门 %s 成功' % id)
                else:
                    flash(u'部门 %s 不存在' % id)
            else:
                Unit.query.add(Unit(caption, details))
                flash(u'新增部门成功')
            return redirect(url_for('.unit'))
    if unit_id:
        u = Unit.query.get(unit_id)
        form.unit_id.data = u.id
        form.caption.data = u.caption
        form.details.data = u.details
    return dict(form=form, units=Unit.query.all())

