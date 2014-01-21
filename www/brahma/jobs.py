__author__ = 'zhengjitang@gmail.com'

import logging,os

import tornado.options

from brahma.env import redis
from brahma.store.site import SettingDao


class TaskSender:
    @staticmethod
    def sitemap():
        redis.lpush("tasks", ("sitemap", None))

    @staticmethod
    def rss():
        redis.lpush("tasks", ("rss", None))

    @staticmethod
    def qr():
        redis.lpush("tasks", ("qr", None))

    @staticmethod
    def echo(message):
        redis.lpush("tasks", ("echo", message))

    @staticmethod
    def email(to, title, body, html=True):
        redis.lpush("tasks", ("email", (to, title, body, html)))


class TaskListener:
    @staticmethod
    def sitemap():
        """
            更新频率：yearly daily, monthly, hourly, weekly
        """
        import datetime, importlib, tornado.options

        with open(TaskListener.__seo_file("sitemap.xml"), "w") as sitemap:
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

            sitemap.write('</urlset>\n')

    @staticmethod
    def rss():
        import importlib, tornado.options

        domain = SettingDao.get("site.domain")
        init = SettingDao.get("site.init")

        with open(TaskListener.__seo_file("rss.xml"), "w") as rss:
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
            rss.write('</rss>\n')


    @staticmethod
    def qr():
        import qrcode
        from brahma.store.site import SettingDao

        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.ERROR_CORRECT_L,
            box_size=10,
            border=1,
        )

        qr.add_data("<a href='http://%s'>%s</a>" % (SettingDao.get("site.domain"), SettingDao.get("site.title")))
        qr.make(fit=True)

        img = qr.make_image()
        img.save(TaskListener.__seo_file("site.png"))

    @staticmethod
    def __seo_file(name):
        from brahma.utils import path
        d = path("../../statics/tmp/seo")
        if not os.path.exists(d):
            os.makedirs(d)
        return d+"/"+name


    @staticmethod
    def email(to, title, body, html):
        smtp = SettingDao.get("site.smtp", encrypt=True)
        if smtp:
            from brahma.utils.email import Email

            email = Email(
                host=smtp['host'],
                username=smtp['username'],
                password=smtp['password'],
                port=smtp['port'],
                ssl=smtp['ssl'],
                debug=tornado.options.options.debug,
            )
            email.send(to, title, body, html)
        else:
            logging.error("SMTP未配置")



