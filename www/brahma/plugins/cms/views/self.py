__author__ = 'zhengjitang@gmail.com'

from brahma.views import BaseHandler

class MainHandler(BaseHandler):
    def get(self):
        self.render("cms/self.html")

handlers = [
    (r"/personal/cms", MainHandler),
]