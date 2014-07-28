#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

import datetime
from flaskext.sqlalchemy import BaseQuery
from webadm.extensions import db, site_id
from sqlalchemy.sql.expression import desc

class SiteQuery(BaseQuery):
    def set_copy_right(self, copy_right):
        s = self.get(site_id)
        s.copy_right = copy_right
        db.session.commit()

    def set_about_me(self, about_me):
        s = self.get(site_id)
        s.about_me = about_me
        db.session.commit()

    def set_admin(self, admin_user, admin_email):
        s = self.get(site_id)
        s.admin_user = admin_user
        s.admin_email = admin_email
        db.session.commit()

    def set_title(self, title):
        s = self.get(site_id)
        s.title = title
        db.session.commit()


class NoteQuery(BaseQuery):
    def all_by_created(self):
        return self.filter(Note.employee_id==None).order_by(desc(Note.created)).all()

    def delete(self, id):
        n = self.get(id)
        if n:
            db.session().delete(n)
            db.session().commit()
            return id
        return None

    def add(self, note):
        db.session.add(note)
        db.session.commit()

    def set_content(self, id, content):
        note = self.get(id)
        note.content = content
        db.session.commit()


class LogQuery(BaseQuery):
    def delete(self, id):
        l = self.get(id)
        if l:
            db.session().delete(l)
            db.session().commit()
            return id
        return None

    def clear(self):
        for l in self.all():
            db.session.delete(l)
        db.session.commit()

    def add(self, log):
        db.session.add(log)
        db.session.commit()


class UserQuery(BaseQuery):
    def del_user(self, id):
        user = self.get(id)
        if user:
            db.session.delete(user)
            db.session.commit()
            return id
        else:
            return None

    def select_by_nick_name(self, nick_name):
        return self.filter_by(nick_name=nick_name).first()

    def set_password(self, id, password):
        user = self.get(id)
        if user:
            user.password = password
            db.session.commit()

    def set_last_login(self, id):
        user = self.get(id)
        if user:
            user.last_login = datetime.date.now()
            db.session.commit()

    def authenticate(self, nick_name, password):
        """
        登录验证，成功返回用户，失败返回None
        """
        user = self.filter_by(nick_name=nick_name).first()
        if user and user.password == password:
            return user
        return None


class Site(db.Model):
    query_class = SiteQuery

    id = db.Column(db.Integer, db.Sequence('site_id_seq'), primary_key=True)
    title = db.Column(db.String)
    about_me = db.Column(db.Text)
    copy_right = db.Column(db.String)
    created = db.Column(db.DateTime)
    last_start = db.Column(db.DateTime)
    admin_user = db.Column(db.String)
    admin_email = db.Column(db.String)

    def __init__(self, title, about_me=None, copy_right=None, created=datetime.datetime.now(), admin_user=None,
                 admin_email=None):
        self.title = title
        self.about_me = about_me
        self.copy_right = copy_right
        self.created = created
        self.admin_email = admin_email
        self.admin_user = admin_user

    def __str__(self):
        return self.title

    def __repr__(self):
        return "<Site('%s', '%s', '%s', '%s', '%s')>" % (
            self.title, self.about_me, self.copy_right, self.created, self.last_start)


class User(db.Model):
    query_class = UserQuery

    id = db.Column(db.Integer, db.Sequence('user_id_seq'), primary_key=True)
    nick_name = db.Column(db.String, nullable=False, unique=True)
    password = db.Column(db.String, nullable=False)
    created = db.Column(db.DateTime)
    last_login = db.Column(db.DateTime)
    notes = db.relationship('Note', backref='author')
    logs = db.relationship('Log', backref='who')
    contact_id = db.Column(db.Integer, db.ForeignKey('contact.id'))
    contact = db.relationship('Contact', backref=db.backref('user', uselist=False))

    def __init__(self, nick_name, password, created=datetime.datetime.now()):
        self.nick_name = nick_name
        self.password = password
        self.created = created

    def __str__(self):
        return self.nick_name

    def __repr__(self):
        return "<User('%s', '%s', '%s', '%s')>" % (self.nick_name, self.created, self.last_login, self.contact_id)


class Log(db.Model):
    query_class = LogQuery

    id = db.Column(db.Integer, db.Sequence('log_id_seq'), primary_key=True)
    message = db.Column(db.String)
    created = db.Column(db.DateTime)
    who_id = db.Column(db.Integer, db.ForeignKey('user.id'))

    def __init__(self, who_id, message, created=datetime.datetime.now()):
        self.who_id = who_id
        self.message = message
        self.created = created

    def __str__(self):
        return '%s: %s' % (self.created, self.message)

    def __repr__(self):
        return "<Log('%s', '%s', '%s')>" % (self.who_id, self.created, self.message)


class Note(db.Model):
    query_class = NoteQuery

    id = db.Column(db.Integer, db.Sequence('log_id_seq'), primary_key=True)
    content = db.Column(db.String)
    created = db.Column(db.DateTime)
    employee_id = db.Column(db.Integer, db.ForeignKey('employee.id'))
    author_id = db.Column(db.Integer, db.ForeignKey('user.id'))


    def __init__(self, author_id, employee_id, content, created=datetime.datetime.now()):
        self.author_id = author_id
        self.employee_id = employee_id
        self.content = content
        self.created = created

    def __str__(self):
        return '%s: %s' % (self.created, self.content)

    def __repr__(self):
        return "<Note('%s', '%s', '%s', '%s')>" % (self.author_id, self.employee_id, self.created, self.content)




    
  