#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flaskext.sqlalchemy import BaseQuery
from webadm.extensions import db

class EmployeeQuery(BaseQuery):
    def search_by_key(self, key):
        from sqlalchemy import or_
        return self.filter(or_(Employee.nick_name.like('%%%s%%' % key), Employee.real_name.like('%%%s%%' % key))).all()

    def set_contact(self, id, qq, msn, email, phone, tel, fax, work, home):
        u = self.get(id)
        if u:
            if not u.contact:
                u.contact = Contact()
            u.contact.qq = qq
            u.contact.msn = msn
            u.contact.email = email
            u.contact.phone = phone
            u.contact.tel = tel
            u.contact.fax = fax
            u.contact.work = work
            u.contact.home = home
            db.session.commit()
            return id
        return None

    def set(self, id, unit_id, nick_name, real_name, details):
        e = self.get(id)
        if e:
            e.unit_id = unit_id
            e.nick_name = nick_name
            e.real_name = real_name
            e.details = details
            db.session.commit()
            return id
        return unit_id

    def get_by_nick_name(self, nick_name):
        return self.filter(Employee.nick_name == nick_name).first()

    def delete(self, id):
        u = self.get(id)
        if u:
            if u.contact:
                db.session.delete(u.contact)
            db.session.delete(u)
            db.session.commit()
            return id
        return None

    def add(self, e):
        db.session.add(e)
        db.session.commit()


class UnitQuery(BaseQuery):
    def delete(self, unit_id):
        unit = self.get(unit_id)
        if unit:
            db.session.delete(unit)
            db.session.commit()
            return unit_id
        return None

    def add(self, unit):
        db.session.add(unit)
        db.session.commit()

    def set(self, unit_id, caption, details):
        unit = self.get(unit_id)
        if unit:
            unit.caption = caption
            unit.details = details
            db.session.commit()
            return unit_id
        return None


class Unit(db.Model):
    query_class = UnitQuery
    id = db.Column(db.Integer, db.Sequence('unit_id_seq'), primary_key=True)
    caption = db.Column(db.String)
    details = db.Column(db.Text)
    employees = db.relationship('Employee', backref='unit')

    def __init__(self, caption, details):
        self.caption = caption
        self.details = details

    def __str__(self):
        return self.caption

    def __repr__(self):
        return "<Unit('%s', '%s')>" % (self.caption, self.details)


class Employee(db.Model):
    query_class = EmployeeQuery

    id = db.Column(db.Integer, db.Sequence('employee_id_seq'), primary_key=True)
    nick_name = db.Column(db.String)
    real_name = db.Column(db.String)
    details = db.Column(db.Text)
    unit_id = db.Column(db.Integer, db.ForeignKey('unit.id'))
    contact_id = db.Column(db.Integer, db.ForeignKey('contact.id'))
    contact = db.relationship('Contact', backref=db.backref('employee', uselist=False))
    notes = db.relationship('Note', backref='employee')

    def __init__(self, unit_id, nick_name, real_name, details):
        self.unit_id = unit_id
        self.nick_name = nick_name
        self.real_name = real_name
        self.details = details

    def __str__(self):
        return u'%s［%s］' % (self.real_name, self.nick_name)

    def __repr__(self):
        return "<Employee('%s', '%s', '%s')>" % (self.nick_name, self.real_name, self.details)


class Contact(db.Model):
    id = db.Column(db.Integer, db.Sequence('contact_id_seq'), primary_key=True)
    email = db.Column(db.String)
    qq = db.Column(db.String)
    msn = db.Column(db.String)
    home = db.Column(db.String)
    tel = db.Column(db.String)
    phone = db.Column(db.String)
    fax = db.Column(db.String)
    work = db.Column(db.String)

    def __str__(self):
        return u'QQ:%s, 手机:%s, 邮箱：%s' % (self.qq, self.phone, self.email)

    def __repr__(self):
        return "<Contact('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s')>" %\
               (self.email, self.qq, self.msn, self.home, self.tel, self.phone, self.fax, self.work)


    
  