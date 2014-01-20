__author__ = 'zhengjitang@gmail.com'

import tornado.web
import tornado.options
from brahma.store.site import SettingDao
from brahma.env import cache


class QrCodeHandler(tornado.web.RequestHandler):
    def get(self):
        from brahma.cache import get_qr_png

        self.set_header("Content-Type", "image/png")
        self.write(get_qr_png())


class SitemapHandler(tornado.web.RequestHandler):
    def get(self):
        @cache.cache("sitemap.xml", expire=3600 * 24)
        def get_sitemap():
            """
            更新频率：yearly daily, monthly, hourly, weekly
            """
            import io, datetime, importlib, tornado.options

            sitemap = io.StringIO()
            sitemap.write('<?xml version="1.0" encoding="UTF-8"?>\n')
            sitemap.write('<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n')
            domain = SettingDao.get("site.domain")
            init = SettingDao.get("site.init")

            items = list()
            items.append(("main", datetime.datetime.now(), "daily", 1.0))
            items.append(("aboutMe", init, "yearly", 0.9))
            items.append(("help", init, "yearly", 0.9))

            for p in tornado.options.options.app_plugins:
                items.extend(importlib.import_module("brahma.plugins." + p).sitemap())
            for loc, lastmod, changefreq, priority in items:
                sitemap.write('\t<url>\n')
                sitemap.write('\t\t<loc>http://%s/%s</loc>\n' % (domain, loc))
                sitemap.write('\t\t<lastmod>%s</lastmod>\n' % lastmod.isoformat())
                sitemap.write('\t\t<changefreq>%s</changefreq>\n' % changefreq)
                sitemap.write('\t\t<priority>%0.1f</priority>\n' % priority)
                sitemap.write('\t</url>\n')

            sitemap.write('</urlset>')
            return sitemap.getvalue()

        self.set_header("Content-Type", "application/xml;charset=UTF-8")
        self.write(get_sitemap())


class RssHandler(tornado.web.RequestHandler):
    def get(self):
        @cache.cache("rss.xml", expire=3600 * 24)
        def get_rss():
            import io, importlib, tornado.options

            domain = SettingDao.get("site.domain")
            init = SettingDao.get("site.init")

            rss = io.StringIO()
            rss.write('<?xml version="1.0" encoding="UTF-8"?>\n')
            rss.write('<rss xmlns:dc="http://purl.org/dc/elements/1.1/" version="2.0">\n')
            rss.write('\t<channel>\n')
            rss.write('\t\t<title>%s</title>\n' % SettingDao.get("site.title"))
            rss.write('\t\t<link>http://%s</link>\n' % domain)
            rss.write('\t\t<description>%s</description>\n' % SettingDao.get("site.description"))

            items = list()
            items.append(("关于我们", "aboutMe", SettingDao.get("site.aboutMe"), init))
            items.append(("帮助文档", "help", SettingDao.get("site.help"), init))

            for p in tornado.options.options.app_plugins:
                items.extend(importlib.import_module("brahma.plugins." + p).rss())

            for title, link, description, pubDate in items:
                link = "http://%s/%s" % (domain, link)
                rss.write('\t\t<item>\n')
                rss.write('\t\t\t<title>%s</title>\n' % title)
                rss.write('\t\t\t<link>%s</link>\n' % link)
                rss.write((
                    '\t\t\t<description>%s</description>\n' % description) if description else '\t\t\t<description/>\n')
                rss.write('\t\t\t<pubDate>%s</pubDate>\n' % init.isoformat())
                rss.write('\t\t\t<guid>%s</guid>\n' % link)
                rss.write('\t\t\t<dc:date>%s</dc:date>\n' % init.isoformat())
                rss.write('\t\t</item>\n')

            rss.write('\t</channel>\n')
            rss.write('</rss>')

            return rss.getvalue()

        self.set_header("Content-Type", "application/xml;charset=UTF-8")
        self.write(get_rss())


class RobotsHandler(tornado.web.RequestHandler):
    def get(self):
        self.write("aaa")


handlers = [
    (r"/robots.txt", RobotsHandler),
    (r"/site.png", QrCodeHandler),
    (r"/rss.xml", RssHandler),
    (r"/sitemap.xml", SitemapHandler),
]