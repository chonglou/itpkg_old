__author__ = 'zhengjitang@gmail.com'

import logging

import tornado.options

from brahma.env import redis as _redis
from brahma.store import Setting as _Setting


class TaskSender:
    @staticmethod
    def robots():
        _redis.lpush("tasks", ("robots", None))

    @staticmethod
    def sitemap():
        _redis.lpush("tasks", ("sitemap", None))

    @staticmethod
    def rss():
        _redis.lpush("tasks", ("rss", None))

    @staticmethod
    def qr():
        _redis.lpush("tasks", ("qr", None))

    @staticmethod
    def echo(message):
        _redis.lpush("tasks", ("echo", message))

    @staticmethod
    def email(to, title, body, html=True):
        _redis.lpush("tasks", ("email", (to, title, body, html)))


class _TaskListener:
    @staticmethod
    def robots():
        import os

        with open(os.path.realpath("statics/robots.txt"), "w") as f:
            f.write("User-agent: *\n")
            f.write("Disallow: /personal/\n")
            f.write("Sitemap: <http://%s/sitemap.xml.gz>\n" % _Setting.get("site.domain"))

    @staticmethod
    def sitemap():
        """
            更新频率：yearly daily, monthly, hourly, weekly
        """
        import datetime, importlib, tornado.options, gzip

        with open(_TaskListener.__seo_file("sitemap.xml"), "w") as sitemap:
            sitemap.write('<?xml version="1.0" encoding="UTF-8"?>\n')
            sitemap.write('<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9">\n')
            domain = _Setting.get("site.domain")
            init = _Setting.get("site.init")

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
        logging.info("生成sitemap.xml完毕,开始压缩")

        with open(_TaskListener.__seo_file("sitemap.xml"), "rb") as f_in:
            with gzip.open(_TaskListener.__seo_file("sitemap.xml.gz"), "wb") as f_out:
                f_out.writelines(f_in)

        logging.info("压缩完毕")

    @staticmethod
    def rss():
        import importlib, tornado.options

        domain = _Setting.get("site.domain")
        init = _Setting.get("site.init")

        with open(_TaskListener.__seo_file("rss.xml"), "w") as rss:
            rss.write('<?xml version="1.0" encoding="UTF-8"?>\n')
            rss.write('<rss xmlns:dc="http://purl.org/dc/elements/1.1/" version="2.0">\n')
            rss.write('\t<channel>\n')
            rss.write('\t\t<title>%s</title>\n' % _Setting.get("site.title"))
            rss.write('\t\t<link>http://%s</link>\n' % domain)
            rss.write('\t\t<description>%s</description>\n' % _Setting.get("site.description"))

            items = list()
            items.append(("关于我们", "aboutMe", _Setting.get("site.aboutMe"), init))
            items.append(("帮助文档", "help", _Setting.get("site.help"), init))

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

        logging.info("生成rss.xml完毕")

    @staticmethod
    def qr():
        import qrcode

        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.ERROR_CORRECT_L,
            box_size=10,
            border=1,
        )

        qr.add_data("<a href='http://%s'>%s</a>" % (_Setting.get("site.domain"), _Setting.get("site.title")))
        qr.make(fit=True)

        img = qr.make_image()
        img.save(_TaskListener.__seo_file("site.png"))
        logging.info("生成site.png完毕")

    @staticmethod
    def __seo_file(name):
        import os

        d = os.path.realpath("statics/tmp/seo")
        if not os.path.exists(d):
            os.makedirs(d)
        return d + "/" + name


    @staticmethod
    def email(to, title, body, html):
        smtp = _Setting.get("site.smtp", encrypt=True)
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


def _init():
    def _new_thread(name, target):
        import threading

        t = threading.Thread(name=name, target=target)
        t.daemon = True
        t.start()

    def _new_process(name, target):
        import multiprocessing, time

        t = multiprocessing.Process(name=name, target=target)
        t.daemon = True
        t.start()
        time.sleep(1)

    def _listener():
        logging.info("启动后台任务进程")
        while True:
            flag, args = _redis.brpop("tasks")
            if flag in ["email","rss", "sitemap", "qr", "robots"]:
                import importlib
                getattr(_TaskListener, flag)(*args)
            elif flag == "echo":
                logging.info(str(args))
            else:
                logging.error("丢弃任务[(%s, %s)]" % (type, str(args)))

    def _scanner():
        logging.info("启动定时扫描进程")
        import time, sched, tornado.options
        from brahma.store import Task

        s = sched.scheduler(time.time, time.sleep)

        def run():
            for t in Task.list_available():
                Task.set_next_run(t.id)
                if t.flag in ['qr', 'sitemap', 'rss', 'robots']:
                    getattr(TaskSender, t.flag)()
                else:
                    logging.error("未知的任务类型[%s]" % t.flag)

        while True:
            s.enter(tornado.options.options.task_space, 10, run)
            s.run()

    _new_thread("task.listener", _listener)
    _new_thread("task.scanner", _scanner)
    _Setting.set("aaa", "bbb")

_init()