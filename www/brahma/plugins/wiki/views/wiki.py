__author__ = 'zhengjitang@gmail.com'

import datetime
from brahma.views import BaseHandler
from brahma.plugins.wiki.store import WikiDao


class WikiHandler(BaseHandler):
    def get(self, name=None):
        if name:
            wiki = WikiDao.get_wiki(name)
            wikiItems = []
            title = wiki.title if wiki else name
        else:
            title = "知识库"
            wikiItems = WikiDao.list_wiki(datetime.datetime.min, datetime.datetime.max)
            wiki = None

        self.render_page("wiki/index.html", title=title, wiki=wiki, wikiItems=wikiItems, index="/wiki/")


handlers = [
    (r"/wiki/(.*)", WikiHandler),
]