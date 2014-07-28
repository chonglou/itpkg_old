#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flaskext.sqlalchemy import BaseQuery
from webadm.extensions import db
import datetime

class NSItemQuery(BaseQuery):
    def delete(self, id):
        ns = self.get(id)
        if ns:
            db.session.delete(ns)
            db.session.commit()
            return id
        return None
    
    def ns_choices(self, host_id):
        choices = []
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if h and h.allow_named and h.named:
            for z in h.named.zones:
                for ns in z.ns_items:
                    choices.append(( ns.id, '%s.%s' % (ns.prefix, z.domain)))
        return choices

    def set_mx(self, id, priority):
        item = self.get(id)
        if item:
            item.mx_priority = priority
            db.session.commit()
            return id
        return None

    def save(self, zone_id, prefix, target):
        item = self.filter(NSItem.zone_id == zone_id).filter(NSItem.prefix == prefix).first()
        if not item:
            item = NSItem()
            item.zone_id = zone_id
            item.prefix = prefix
            item.target = target
            db.session.add(item)
        else:
            item.target = target
        db.session.commit()


class ZoneQuery(BaseQuery):
    def delete(self, id):
        z = self.get(id)
        if z:
            db.session.delete(z)
            db.session.commit()
            return id
        return None
    
    def save(self, named_id, zone_id, domain):
        z = self.filter(Zone.named_id == named_id).filter(Zone.domain == domain).first()
        if zone_id and z and z.id == zone_id:
            return True

        if not zone_id and not z:
            z = Zone()
            z.named_id = named_id
            z.domain = domain
            z.last_edit = datetime.datetime.now()
            db.session.add(z)
            db.session.commit()
            return True

        if zone_id and not z:
            zone = self.get(zone_id)
            if zone:
                zone.domain = domain
                zone.last_edit = datetime.datetime.now()
                db.session.commit()
                return True
        return False


class NamedQuery(BaseQuery):
    def set(self, id, listen_on_ips, forwarders, controls):
        n = self.get(id)
        if n:
            n.listen_on_ips = listen_on_ips
            n.forwarders = forwarders
            n.controls = controls
            db.session.commit()
            return id
        return None

    def save(self, host_id, caption, details):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if h:
            if h.allow_named:
                if not h.named:
                    h.named = Named()
                h.named.caption = caption
                h.named.details = details
                db.session.commit()
                return True
        return False


class Named(db.Model):
    query_class = NamedQuery

    id = db.Column(db.Integer, db.Sequence('named_id_seq'), primary_key=True)
    caption = db.Column(db.String)
    details = db.Column(db.Text)
    listen_on_ips = db.Column(db.String)
    forwarders = db.Column(db.String)
    controls = db.Column(db.String)
    zones = db.relationship("Zone", backref='named')

    def __str__(self):
        return self.caption

    def __repr__(self):
        return "<Named('%s', '%s', '%s', '%s')>" % (self.caption, self.details, self.listen_on_ips, self.controls)


class Zone(db.Model):
    query_class = ZoneQuery

    id = db.Column(db.Integer, db.Sequence('zone_id_seq'), primary_key=True)
    domain = db.Column(db.String)
    named_id = db.Column(db.Integer, db.ForeignKey('named.id'))
    ns_items = db.relationship("NSItem", backref='zone')
    last_edit = db.Column(db.DateTime)

    def __str__(self):
        return self.domain

    def __repr__(self):
        return "<Zone('%s', '%s', '%s')>" % (self.named_id, self.domain, self.last_edit)


class NSItem(db.Model):
    query_class = NSItemQuery

    id = db.Column(db.Integer, db.Sequence('a_item_id_seq'), primary_key=True)
    target = db.Column(db.String)
    prefix = db.Column(db.String)
    zone_id = db.Column(db.Integer, db.ForeignKey('zone.id'))
    mx_priority = db.Column(db.Integer)

    def __str__(self):
        return self.mx_priority and u'%s => %s［MAIL：%s］' % (
        self.prefix, self.target, self.mx_priority) or u'%s => %s' % (self.prefix, self.target)

    def __repr__(self):
        return '<NSItem(%s, %s, %s, %s)>' % (self.zone_id, self.prefix, self.mx_priority, self.target)




  