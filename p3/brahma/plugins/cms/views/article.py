__author__ = 'zhengjitang@gmail.com'

from brahma.views import BaseHandler


class ArticleHandler(BaseHandler):
    def get(self):
        pass


handlers = [
    (r"/cms/article/([0-9]+)", ArticleHandler),
]