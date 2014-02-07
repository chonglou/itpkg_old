__author__ = 'zhengjitang@gmail.com'

import pickle, datetime
from brahma.env import transaction
from brahma.models import Item
from brahma.store import HistoryDao
from brahma.utils.database import select, update, insert, delete
from brahma.plugins.wiki.models import Wiki


class WikiDao(object):
    @staticmethod
    @transaction(False)
    def set(name, title, body, author=None, cursor=None):
        w = WikiDao._get(name, cursor)
        if w:
            WikiDao._backup(w, cursor)
            update(Item(title=title, body=body, last_edit=datetime.datetime.now()).update("wikis", id_val=name,
                                                                                          id_name="name",
                                                                                          version=True))(cursor)
        else:
            insert(Item(name=name, title=title, body=body, last_edit=datetime.datetime.now(), author=author).insert(
                "wikis"))(cursor)

    @staticmethod
    @transaction(False)
    def delete(name, cursor=None):
        w = WikiDao._get(name, cursor)
        wid = w.id
        WikiDao._backup(w, cursor)
        delete(Item(id=wid).delete("wikis"))(cursor)

    @staticmethod
    def _backup(w, cursor):
        url = "wiki://%s" % w.id
        version = w.version
        delattr(w, "id")
        delattr(w, "version")
        HistoryDao._add(url, pickle.dumps(w.__dict__), version=version, cursor=cursor)
        return w.author

    @staticmethod
    @transaction()
    def get(name, cursor=None):
        w = select(Item(name=name).select("wikis", ["title", "body", "author", "last_edit"]),
                   one=True)(cursor)
        return Wiki(name, *w) if w else None

    @staticmethod
    def _get(name, cursor):
        w = select(Item(name=name).select("wikis", ["id", "name", "title", "body", "author", "created", "version"]),
                   one=True)(cursor)
        if w:
            wid, name, title, body, author, created, version = w
            return Item(id=wid, name=name, title=title, body=body, author=author, created=created, version=version)
        return None


    @staticmethod
    @transaction()
    def list_page(begin, end, author=None, cursor=None):
        args = [begin, end]
        if author:
            args.append(author)
        cursor.execute("SELECT name_,title_,created_ FROM wikis WHERE last_edit_>%s AND created_<%s" +
                       ("AND author_=%s" if author else ""),
                       args)
        return cursor.fetchall()