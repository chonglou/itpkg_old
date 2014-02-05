__author__ = 'zhengjitang@gmail.com'

from brahma.views import BaseHandler


class TagHandler(BaseHandler):
    def get(self):
        pass


handlers = [
    (r"/cms/tag/([0-9]+)", TagHandler),
]