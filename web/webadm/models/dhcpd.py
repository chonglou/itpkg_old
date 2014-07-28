#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flaskext.sqlalchemy import BaseQuery
from webadm.extensions import db


class Dhcpd4Query(BaseQuery):
    def setup(self, host_id, domain, lan_net, dns_1, dns_2):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_dhcpd4 or not h.dhcpd4:
            return False
        h.dhcpd4.domain = domain
        h.dhcpd4.lan_net = lan_net
        h.dhcpd4.dns_1 = dns_1
        h.dhcpd4.dns_2 = dns_2
        db.session.commit()
        return host_id

    def save(self, host_id, caption, details):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_dhcpd4:
            return False
        if not h.dhcpd4:
            h.dhcpd4 = Dhcpd4(caption, details)
        else:
            h.dhcpd4.caption = caption
            h.dhcpd4.details = details
        db.session.commit()
        return host_id


class Dhcpd4(db.Model):
    query_class = Dhcpd4Query
    id = db.Column(db.Integer, db.Sequence('dhcpd4_id_seq'), primary_key=True)
    caption = db.Column(db.String)
    details = db.Column(db.Text)
    domain = db.Column(db.String)
    lan_net = db.Column(db.String)
    dns_1 = db.Column(db.String)
    dns_2 = db.Column(db.String)

    def __init__(self, caption, details):
        self.caption = caption
        self.details = details

    def __str__(self):
        return self.caption

    def __repr__(self):
        return "<Dhcpd4('%s', '%s', '%s' )>" % (self.caption, self.details, self.dns_ips)



