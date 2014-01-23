__author__ = 'zhengjitang@gmail.com'

import datetime

from sqlalchemy import Column, Integer, String, DateTime, Text, Sequence

from brahma.models import Base


class Wiki(Base):
    __tablename__ = "wikis"
    name = Column(String(255), Sequence('wiki_id_seq'), primary_key=True, nullable=False)
    title = Column(String(255), nullable=False)
    body = Column(Text, nullable=False)
    author = Column(Integer, nullable=False)
    created = Column(DateTime, nullable=False, default=datetime.datetime.now())
    lastEdit = Column(DateTime)

    def __init__(self, name, title, body, author):
        self.name = name
        self.title = title
        self.body = body
        self.author = author


