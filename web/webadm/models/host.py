#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flaskext.sqlalchemy import BaseQuery
from webadm.extensions import db

class MacInfoQuery(BaseQuery):
    def search_by_key(self, key):
        from sqlalchemy import or_
        return self.filter(or_(MacInfo.mac.like('%%%s%%' % key), MacInfo.ip.like('%%%s%%' % key))).all()

    def set_limit(self, mac_id, limit_id):
        mac = self.get(mac_id)
        if mac:
            mac.limit_id = limit_id
            db.session.commit()
            return True
        return False

    def bind(self, mac_id, ip, bind):
        mac = self.get(mac_id)
        if not self.filter(MacInfo.ip == ip).filter(MacInfo.bind == bind).first():
            if mac:
                mac.ip = ip
                mac.bind = bind
                db.session.commit()
                return True
        return False

    def delete(self, mac_id):
        m = self.get(mac_id)
        if m:
            db.session.delete(m)
            db.session.commit()
            return mac_id
        return None

    def set(self, id, employee_id, allow):
        m = self.get(id)
        if m:
            m.employee_id = employee_id
            m.allow = allow
            db.session.commit()
            return id
        return None

    def sync(self, host_id, macs):
        add = 0
        edit = 0
        for mac in macs.keys():
            ip = macs[mac]
            m = self.filter(MacInfo.host_id == host_id).filter(MacInfo.mac == mac).first()
            if m:
                if m.ip != ip:
                    m.ip = ip
                    edit += 1
            else:
                db.session.add(MacInfo(host_id, mac, ip))
                add += 1
        db.session.commit()
        return add, edit


class HostQuery(BaseQuery):
    def delete(self, host_id):
        h = self.get(host_id)
        if h:
            if h.vpn:
                db.session.delete(h.vpn)
            if h.dhcpd4:
                db.session.delete(h.dhcpd4)
            if h.named:
                db.session.delete(h.named)
            if h.firewall:
                db.session.delete(h.firewall)
            if h.flow_ctl:
                db.session.delete(h.flow_ctl)
            db.session.delete(h)
            db.session.commit()
            return host_id
        return None

    def save(self, host_id, caption, details, allow_dhcpd4, allow_mail, allow_named, allow_vpn, allow_firewall,
             listen_ip, manager_ip):
        h = None
        if host_id:
            h = self.get(host_id)
            if not h:
                return False

        if not h:
            h = Host()
            db.session.add(h)
        h.caption = caption
        h.details = details
        h.allow_dhcpd4 = allow_dhcpd4
        h.allow_mail = allow_mail
        h.allow_named = allow_named
        h.allow_vpn = allow_vpn
        h.allow_firewall = allow_firewall
        h.listen_ip = listen_ip
        h.manager_ip = manager_ip
        db.session.commit()
        return True


class Host(db.Model):
    query_class = HostQuery
    id = db.Column(db.Integer, db.Sequence('host_id_seq'), primary_key=True)
    caption = db.Column(db.String)
    details = db.Column(db.Text)
    allow_dhcpd4 = db.Column(db.Boolean)
    allow_named = db.Column(db.Boolean)
    allow_firewall = db.Column(db.Boolean)
    allow_vpn = db.Column(db.Boolean)
    allow_mail = db.Column(db.Boolean)
    dhcpd4_id = db.Column(db.Integer, db.ForeignKey('dhcpd4.id'))
    dhcpd4 = db.relationship('Dhcpd4', backref=db.backref('host', uselist=False))
    named_id = db.Column(db.Integer, db.ForeignKey('named.id'))
    named = db.relationship('Named', backref=db.backref('host', uselist=False))
    vpn_id = db.Column(db.Integer, db.ForeignKey('vpn.id'))
    vpn = db.relationship('Vpn', backref=db.backref('host', uselist=False))
    firewall_id = db.Column(db.Integer, db.ForeignKey('firewall.id'))
    firewall = db.relationship('Firewall', backref=db.backref('host', uselist=False))
    mail_id = db.Column(db.Integer, db.ForeignKey('mail.id'))
    mail_ctl = db.relationship('Mail', backref=db.backref('host', uselist=False))
    macs = db.relationship('MacInfo', backref='host')
    listen_ip = db.Column(db.String)
    manager_ip = db.Column(db.String)


    def __str__(self):
        return self.caption

    def __repr__(self):
        return "<Host('%s', '%s')>" % (self.caption, self.details)


class MacInfo(db.Model):
    query_class = MacInfoQuery
    id = db.Column(db.Integer, db.Sequence('mac_id_seq'), primary_key=True)
    mac = db.Column(db.String, nullable=False)
    ip = db.Column(db.String)
    host_name = db.Column(db.String)
    employee_id = db.Column(db.Integer, db.ForeignKey("employee.id"))
    employee = db.relationship('Employee', backref='macs')
    host_id = db.Column(db.Integer, db.ForeignKey('host.id'))
    allow = db.Column(db.Boolean)
    bind = db.Column(db.Boolean)
    limit_id = db.Column(db.Integer, db.ForeignKey('limit_rule.id'))
    limit = db.relationship('LimitRule', backref='macs')

    def __init__(self, host_id, mac, ip, host_name=None, allow=False, bind=False):
        self.host_id = host_id
        self.mac = mac
        self.ip = ip
        self.host_name = host_name
        self.allow = allow
        self.bind = bind

    def __str__(self):
        return u'(%s, %s, %s)' % (self.ip, self.mac, self.employee)

    def __repr__(self):
        return "<MacInfo('%s', '%s', '%s', '%s', '%s', '%s')>" % (
            self.host_id, self.mac, self.ip, self.host_name, self.allow, self.bind)




