__author__ = 'zhengjitang@gmail.com'

import datetime

from sqlalchemy import Column, Integer, String, DateTime, Sequence, Text,Boolean

from brahma.models import Base


class Tag(Base):
    __tablename__ = "cms_tags"
    id = Column(Integer, Sequence('cms_tag_id_seq'), primary_key=True)
    name = Column(String(255), nullable=False, unique=True)
    keep = Column(Boolean, nullable=False)
    visits = Column(Integer, nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())

    def __init__(self, name, keep):
        self.name = name
        self.keep = keep


class Article(Base):
    __tablename__ = "cms_articles"
    id = Column(Integer, Sequence('cms_article_id_seq'), primary_key=True)
    author = Column(Integer, nullable=False)
    logo = Column(String(255))
    title = Column(String(255), nullable=False)
    summary = Column(String(800), nullable=False)
    body = Column(Text, nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())
    lastEdit = Column(DateTime)
    visits = Column(Integer, nullable=False)

    def __init__(self, author, title, summary, body, logo):
        self.author = author
        self.title = title
        self.summary = summary
        self.body = body
        self.logo = logo


class ArticleTag(Base):
    __tablename__ = "cms_article_tag"
    id = Column(Integer, Sequence('cms_article_tag_id_seq'), primary_key=True)
    article = Column(Integer, nullable=False)
    tag = Column(Integer, nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())

    def __init__(self, article, tag):
        self.article = article
        self.tag = tag


class Comment(Base):
    __tablename__ = "cms_comments"
    id = Column(Integer, Sequence('cms_comments_id_seq'), primary_key=True)
    article = Column(Integer, nullable=False)
    content = Column(Text, nullable=False)
    comment = Column(Integer)
    user = Column(Integer, nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())
    lastEdit = Column(DateTime)

    def __init__(self, user, article, comment, content):
        self.user = user
        self.article = article
        self.comment = comment
        self.content = content

