#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flaskext.sqlalchemy import BaseQuery
from webadm.extensions import db

class LimitRuleQuery(BaseQuery):
    def delete_link(self, item_id, mac_id):
        from webadm.models.host import MacInfo

        r = self.get(item_id)
        m = MacInfo.query.get(mac_id)
        if r and m and r.id == m.limit_id:
            m.limit = None
            db.session.commit()
            return True
        return False

    def save(self, item_id, firewall_id, caption, up_rate, down_rate, up_ceil, down_ceil):
        r = self.filter(LimitRule.firewall_id==firewall_id).filter(LimitRule.caption == caption).first()

        if item_id:
            item = self.get(item_id)
        else:
            item = None

        if r:
            if item and item.id == r.id:
                item.up_rate = up_rate
                item.down_rate = down_rate
                item.up_ceil = up_ceil
                item.down_ceil = down_ceil
                db.session.commit()
                return True
        else:
            if item:
                item.caption = caption
                item.up_rate = up_rate
                item.down_rate = down_rate
                item.up_ceil = up_ceil
                item.down_ceil = down_ceil
                db.session.commit()
            else:
                rule = LimitRule(firewall_id, caption, up_rate, down_rate, up_ceil, down_ceil)
                db.session.add(rule)
            db.session.commit()
            return True

        return False

    def delete(self, id):
        r = self.get(id)
        if r:
            db.session.delete(r)
            db.session.commit()
            return id
        return None


class NatRuleQuery(BaseQuery):
    def batch_save(self, firewall_id, s_ip, start_port, stop_port, protocol, d_ip ):
        save_list = []
        for port in range(start_port, stop_port+1):
            r = self.filter(NatRule.firewall_id==firewall_id).filter(NatRule.s_ip == s_ip).filter(
                NatRule.s_port == port).filter(
                NatRule.protocol == protocol).first()
            if r:
                return None
            save_list.append(NatRule(firewall_id, s_ip, port, protocol, d_ip, port))
        for r in save_list:
            db.session.add(r)
        db.session.commit()
        return len(save_list)


    def save(self, item_id, firewall_id, s_ip, s_port, protocol, d_ip, d_port):
        r = self.filter(NatRule.firewall_id == firewall_id).filter(NatRule.s_ip == s_ip).filter(
            NatRule.s_port == s_port).filter(
            NatRule.protocol == protocol).first()

        if item_id:
            item = self.get(item_id)
        else:
            item = None

        if r:
            if item and item.id == r.id:
                item.d_ip = d_ip
                item.d_port = d_port
                db.session.commit()
                return True
        else:
            if item:
                item.firewall_id = firewall_id
                item.s_ip = s_ip
                item.s_port = s_port
                item.protocol = protocol
                item.d_ip = d_ip
                item.d_port = d_port
            else:
                db.session.add(NatRule(firewall_id, s_ip, s_port, protocol, d_ip, d_port))
            db.session.commit()
            return True

        return False

    def delete(self, id):
        r = self.get(id)
        if r:
            db.session.delete(r)
            db.session.commit()
            return id
        return None


class OutputRuleQuery(BaseQuery):
    def delete_else(self, item_id, mac_id):
        from webadm.models.host import MacInfo

        r = self.get(item_id)
        m = MacInfo.query.get(mac_id)
        if r and m and m in r.macs:
            r.macs.remove(m)
            db.session.commit()
            return True
        return False

    def set_else(self, item_id, mac_id):
        from webadm.models.host import MacInfo

        r = self.get(item_id)
        m = MacInfo.query.get(mac_id)
        if r and m and m not in r.macs:
            r.macs.append(m)
            db.session.commit()
            return True
        return False

    def save(self, item_id, firewall_id, domain, start, end, mon, tue, wed, thu, fri, sat, sun):
        r = self.filter(OutputRule.firewall_id == firewall_id).filter(OutputRule.domain == domain).filter(
            OutputRule.start == start).filter(OutputRule.end == end).first()

        if item_id:
            item = self.get(item_id)
        else:
            item = None

        if r:
            if item and item.id == r.id:
                item.mon = mon
                item.tue = tue
                item.wed = wed
                item.thu = thu
                item.fri = fri
                item.sat = sat
                item.sun = sun
                db.session.commit()
                return True
        else:
            if item:
                item.domain = domain
                item.start = start
                item.end = end
                item.mon = mon
                item.tue = tue
                item.wed = wed
                item.thu = thu
                item.fri = fri
                item.sat = sat
                item.sun = sun
            else:
                db.session.add(OutputRule(firewall_id, domain, start, end, mon, tue, wed, thu, fri, sat, sun))
            db.session.commit()
            return True

        return False

    def delete(self, id):
        r = self.get(id)
        if r:
            db.session.delete(r)
            db.session.commit()
            return id
        return None


class InputRuleQuery(BaseQuery):
    def save(self, item_id, firewall_id, s_ip, protocol, d_ip, d_port):
        r = self.filter(InputRule.firewall_id == firewall_id).filter(InputRule.s_ip == s_ip).filter(
            InputRule.protocol == protocol).filter(InputRule.d_ip == d_ip).filter(InputRule.d_port == d_port).first()

        if item_id:
            item = self.get(item_id)
        else:
            item = None

        if r:
            if item and item.id == r.id:
                return True
        else:
            if item:
                item.firewall_id = firewall_id
                item.s_ip = s_ip
                item.protocol = protocol
                item.d_ip = d_ip
                item.d_port = d_port
            else:
                db.session.add(InputRule(firewall_id, s_ip, protocol, d_ip, d_port))
            db.session.commit()
            return True

        return False

    def delete(self, id):
        r = self.get(id)
        if r:
            db.session.delete(r)
            db.session.commit()
            return id
        return None


class FirewallQuery(BaseQuery):
    def set_default_limit(self, id, limit_id):
        ff = self.get(id)
        if ff:
            ff.limit_id = limit_id
            db.session.commit()
            return True
        return False

    def save_allow_black_list(self, host_id, allow):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall or not h.firewall:
            return False
        h.firewall.allow_black_list = allow
        db.session.commit()
        return host_id

    def save_dmz(self, host_id, device, net):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall or not h.firewall or not h.firewall.allow_dmz:
            return False
        h.firewall.dmz_device = device
        h.firewall.dmz_net = net
        db.session.commit()
        return host_id

    def save_allow_dmz(self, host_id, allow):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall or not h.firewall:
            return False
        h.firewall.allow_dmz = allow
        db.session.commit()
        return host_id

    def save_lan(self, host_id, device, net):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall or not h.firewall or not h.firewall.allow_lan:
            return False
        h.firewall.lan_device = device
        h.firewall.lan_net = net
        db.session.commit()
        return host_id

    def save_allow_lan(self, host_id, allow):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall or not h.firewall:
            return False
        h.firewall.allow_lan = allow
        db.session.commit()
        return host_id

    def save_ping(self, host_id, allow):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall or not h.firewall:
            return False
        h.firewall.allow_ping = allow
        db.session.commit()
        return host_id

    def save_wan(self, host_id, device, ips):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall or not h.firewall:
            return False
        h.firewall.wan_device = device
        h.firewall.wan_ips = ips
        db.session.commit()
        return host_id

    def save(self, host_id, caption, details):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_firewall:
            return False
        if not h.firewall:
            h.firewall = Firewall(caption, details)
        else:
            h.firewall.caption = caption
            h.firewall.details = details
        db.session.commit()
        return host_id

mac_output_else_table = db.Table('mac_output_else',
                                 db.Column('mac_id', db.Integer, db.ForeignKey('mac_info.id')),
                                 db.Column('output_id', db.Integer, db.ForeignKey('output_rule.id'))
)

class Firewall(db.Model):
    query_class = FirewallQuery
    IP_SPLIT=';'

    id = db.Column(db.Integer, db.Sequence('firewall_id_seq'), primary_key=True)
    caption = db.Column(db.String)
    details = db.Column(db.Text)
    allow_ping = db.Column(db.Boolean)
    is_black_list = db.Column(db.Boolean)
    allow_lan = db.Column(db.Boolean)
    allow_nat = db.Column(db.Boolean)
    allow_dmz = db.Column(db.Boolean)
    wan_ips = db.Column(db.String)
    wan_device = db.Column(db.String)
    dmz_device = db.Column(db.String)
    dmz_net = db.Column(db.String)
    lan_device = db.Column(db.String)
    lan_net = db.Column(db.String)
    outputs = db.relationship("OutputRule", backref='firewall')
    inputs = db.relationship("InputRule", backref='firewall')
    nats = db.relationship("NatRule", backref='firewall')
    limits = db.relationship("LimitRule", backref='firewall')
    limit_id = db.Column(db.Integer)

    def lan_net_shift(self):
        return '.'.join(self.lan_net.split('.')[0:3])

    def wan_ip_list(self):
        if self.wan_ips:
            return [x.strip() for x in self.wan_ips.split(Firewall.IP_SPLIT)]
        return []

    def __init__(self, caption, details):
        self.caption = caption
        self.details = details

    def __str__(self):
        return self.caption

    def __repr__(self):
        return "<Firewall(%s, %s, %s, %s, %s)>" % (self.caption, self.details, self.wan_ips, self.lan_net, self.dmz_net)


class InputRule(db.Model):
    query_class = InputRuleQuery
    id = db.Column(db.Integer, db.Sequence('input_rule_id_seq'), primary_key=True)
    s_ip = db.Column(db.String)
    protocol = db.Column(db.String)
    d_ip = db.Column(db.String)
    d_port = db.Column(db.Integer)
    firewall_id = db.Column(db.Integer, db.ForeignKey('firewall.id'))

    def __init__(self, firewall_id, s_ip, protocol, d_ip, d_port):
        self.firewall_id = firewall_id
        self.s_ip = s_ip
        self.protocol = protocol
        self.d_ip = d_ip
        self.d_port = d_port

    def __str__(self):
        return '%s =%s=> %s:%s' % (self.s_ip, self.protocol, self.d_ip, self.d_port)

    def __repr__(self):
        return "<InputItem('%s', '%s', '%s', '%s', '%s')>" % (
            self.firewall_id, self.s_ip, self.protocol, self.d_ip, self.d_port)


class OutputRule(db.Model):
    query_class = OutputRuleQuery
    id = db.Column(db.Integer, db.Sequence('output_rule_id_seq'), primary_key=True)
    domain = db.Column(db.String)
    start = db.Column('start_time', db.String)
    end = db.Column('end_time', db.String)
    mon = db.Column(db.Boolean)
    tue = db.Column(db.Boolean)
    wed = db.Column(db.Boolean)
    thu = db.Column(db.Boolean)
    fri = db.Column(db.Boolean)
    sat = db.Column(db.Boolean)
    sun = db.Column(db.Boolean)
    firewall_id = db.Column(db.Integer, db.ForeignKey('firewall.id'))
    macs = db.relationship('MacInfo', secondary=lambda:mac_output_else_table, backref='outputs')

    def weekdays_cn(self):
        wk = []
        if self.mon:
            wk.append(u'星期一')
        if self.tue:
            wk.append(u'星期二')
        if self.wed:
            wk.append(u'星期三')
        if self.thu:
            wk.append(u'星期四')
        if self.fri:
            wk.append(u'星期五')
        if self.sat:
            wk.append(u'星期六')
        if self.sun:
            wk.append(u'星期日')

        return ','.join(wk)

    def weekdays(self):
        wk = []
        if self.mon:
            wk.append('Mon')
        if self.tue:
            wk.append('Tue')
        if self.wed:
            wk.append('Wed')
        if self.thu:
            wk.append('Thu')
        if self.fri:
            wk.append('Fri')
        if self.sat:
            wk.append('Sat')
        if self.sun:
            wk.append('Sun')

        if wk:
            return ','.join(wk)
        return None

    def __init__(self, firewall_id, domain, start, end,
                 mon=True, tue=True, wed=True, thu=True, fri=True, sat=True, sun=True):
        self.firewall_id = firewall_id
        self.domain = domain
        self.start = start
        self.end = end
        self.mon = mon
        self.tue = tue
        self.wed = wed
        self.thu = thu
        self.fri = fri
        self.sat = sat
        self.sun = sun

    def __str__(self):
        return u'%s［%s-%s］［%s］' % (self.domain, self.start, self.end, self.weekdays_cn())

    def __repr__(self):
        return "<OutputRule('%s', '%s', '%s', '%s', '%s')>" % (
            self.firewall_id, self.domain, self.start, self.end, self.weekdays())


class NatRule(db.Model):
    query_class = NatRuleQuery
    id = db.Column(db.Integer, db.Sequence('nat_rule_id_seq'), primary_key=True)
    s_ip = db.Column(db.String)
    s_port = db.Column(db.Integer)
    d_ip = db.Column(db.String)
    d_port = db.Column(db.Integer)
    protocol = db.Column(db.String)
    firewall_id = db.Column(db.Integer, db.ForeignKey('firewall.id'))

    def __init__(self, firewall_id, s_ip, s_port, protocol, d_ip, d_port):
        self.firewall_id = firewall_id
        self.protocol = protocol
        self.s_ip = s_ip
        self.s_port = s_port
        self.d_ip = d_ip
        self.d_port = d_port

    def __str__(self):
        return '%s:%s =%s=> %s:%s' % (self.s_ip, self.s_port, self.protocol, self.d_ip, self.d_port)

    def __repr__(self):
        return "<NatRule('%s', '%s', '%s', '%s', '%s', '%s')>" % (
            self.firewall_id, self.s_ip, self.s_port, self.protocol, self.d_ip, self.d_port)


class LimitRule(db.Model):
    query_class = LimitRuleQuery
    id = db.Column(db.Integer, db.Sequence('limit_rule_id_seq'), primary_key=True)
    caption = db.Column(db.String)
    down_rate = db.Column(db.Integer)
    up_rate = db.Column(db.Integer)
    down_ceil = db.Column(db.Integer)
    up_ceil = db.Column(db.Integer)
    firewall_id = db.Column(db.Integer, db.ForeignKey('firewall.id'))

    def __init__(self, firewall_id, caption, up_rate, down_rate, up_ceil, down_ceil):
        self.firewall_id = firewall_id
        self.caption = caption
        self.up_rate = up_rate
        self.down_rate = down_rate
        self.up_ceil = up_ceil
        self.down_ceil = down_ceil

    def __str__(self):
        return u'%s［(%s, %s), (%s, %s)］' % (self.caption, self.up_rate, self.down_rate, self.up_ceil, self.down_ceil)

    def __repr__(self):
        return "<LimitRule(''%s', %s', '%s', '%s', '%s', '%s')>" % (
            self.firewall_id, self.caption, self.up_rate, self.down_rate, self.up_ceil, self.down_ceil)

