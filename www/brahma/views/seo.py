__author__ = 'zhengjitang@gmail.com'

import tornado.web
import tornado.options


class AttachHandler(tornado.web.RequestHandler):
    def get(self, name):
        self.redirect("/static/tmp/attach/%s" % name, permanent=True)


handlers = [
    (r"/attachments/(.*)", AttachHandler),
    (r"/site.png", tornado.web.RedirectHandler, {"url": "/static/tmp/seo/site.png"}),
    (r"/rss.xml", tornado.web.RedirectHandler, {"url": "/static/tmp/seo/rss.xml"}),
    (r"/sitemap.xml", tornado.web.RedirectHandler, {"url": "/static/tmp/seo/sitemap.xml"}),
]