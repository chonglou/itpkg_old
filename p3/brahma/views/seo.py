__author__ = 'zhengjitang@gmail.com'

import tornado.web
import tornado.options
from brahma.env import attach_dir


class GoogleHandler(tornado.web.RequestHandler):
    def get(self, name):
        from brahma.cache import get_site_info

        if name == get_site_info("seo.google"):
            self.set_header("Content-Type:", "text/plain")
            self.write("google-site-verification: google%s.html" % name)


class BaiduHandler(tornado.web.RequestHandler):
    def get(self, name):
        from brahma.cache import get_site_info

        if name == get_site_info("seo.baidu"):
            self.set_header("Content-Type:", "text/plain")
            self.write(name)


handlers = [
    (r"/google(.*).html", GoogleHandler),
    (r"/baidu_verify_(.*).html", BaiduHandler),
    (r"/attachments/(.*)", tornado.web.StaticFileHandler, dict(path=attach_dir)),
    (r"/rss.xml", tornado.web.RedirectHandler, {"url": "/attachments/seo/rss.xml"}),
    (r"/sitemap.xml", tornado.web.RedirectHandler, {"url": "/attachments/seo/sitemap.xml"}),
    (r"/sitemap.xml.gz", tornado.web.RedirectHandler, {"url": "/attachments/seo/sitemap.xml.gz"}),
]