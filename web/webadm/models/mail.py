#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flaskext.sqlalchemy import BaseQuery
from webadm.extensions import db

class MailQuery(BaseQuery):
    def set(self, host_id, caption, details, db_host, db_port, db_name, db_user, db_password):
        from webadm.models.host import Host

        h = Host.query.get(host_id)
        if not h or not h.allow_vpn:
            return False
        if not h.vpn:
            h.mail = Mail()
        h.mail.caption = caption
        h.mail.details = details
        h.mail.db_host = db_host
        h.mail.db_port = db_port
        h.mail.db_name = db_name
        h.mail.db_user = db_user
        h.mail.db_password = db_password
        db.session.commit()
        return host_id


class Mail(db.Model):
    query_class = MailQuery
    id = db.Column(db.Integer, db.Sequence('mail_id_seq'), primary_key=True)
    caption = db.Column(db.String)
    details = db.Column(db.Text)
    db_host = db.Column(db.String)
    db_name = db.Column(db.String)
    db_user = db.Column(db.String)
    db_password = db.Column(db.String)
    db_port = db.Column(db.Integer)

    def __init__(self, caption=None, details=None):
        self.caption = caption
        self.details = details

    def __str__(self):
        return self.caption

    def __repr__(self):
        return "<Mail('%s', '%s', 'mysql://%s:%s/%s:%s@%s')>" %\
               (self.caption, self.details, self.db_host, self.db_port, self.db_name, self.db_user, self.db_password)