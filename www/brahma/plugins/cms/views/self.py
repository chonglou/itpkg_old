__author__ = 'zhengjitang@gmail.com'

from brahma.views import BaseHandler


class MainHandler(BaseHandler):
    def get(self):
        self.render_page("cms/index.html", title="文章列表", index="/cms/")


handlers = [
    (r"/cms/", MainHandler),
]