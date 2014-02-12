__author__ = 'zhengjitang@gmail.com'

import logging

from brahma.models import TaskFlag


class _TaskQueueRedis:
    def __init__(self):
        import tornado.options
        from brahma.utils.redis import Redis

        self.__redis = Redis(
            name=tornado.options.options.app_name,
            host=tornado.options.options.redis_host,
            port=tornado.options.options.redis_port
        )

    def put(self, flag, args):
        self.__redis.lpush("tasks", (flag, args))

    def get(self):
        return self.__redis.brpop("tasks")


class _TaskQueue:
    def __init__(self):
        import queue

        self.__q = queue.Queue()

    def put(self, flag, args):
        self.__q.put_nowait((flag, args))

    def get(self):
        return self.__q.get()


_Queue = _TaskQueue()


class TaskSender:
    @staticmethod
    def robots():
        TaskSender.__push(TaskFlag.ROBOTS, None)

    @staticmethod
    def sitemap():
        TaskSender.__push(TaskFlag.SITEMAP, None)

    @staticmethod
    def rss():
        TaskSender.__push(TaskFlag.RSS, None)

    @staticmethod
    def qr():
        TaskSender.__push(TaskFlag.QR, None)

    @staticmethod
    def echo(message):
        TaskSender.__push(TaskFlag.ECHO, message)

    @staticmethod
    def email(to, title, body, html=True):
        TaskSender.__push(TaskFlag.EMAIL, (to, title, body, html))

    @staticmethod
    def __push(flag, args):
        _Queue.put(flag, args)


class _TaskListener:
    @staticmethod
    def echo(message):
        import logging

        logging.info("ECHO: %s" % message)

    @staticmethod
    def robots():
        import os
        from brahma.store import SettingDao

        with open(os.path.realpath("statics/robots.txt"), "w") as f:
            f.write("User-agent: *\n")
            f.write("Disallow: /personal/\n")
            f.write("Sitemap: <http://%s/sitemap.xml.gz>\n" % SettingDao.get("site.domain"))

    @staticmethod
    def sitemap():
        """
            更新频率：yearly daily, monthly, hourly, weekly
        """
        import datetime, importlib, tornado.options, gzip
        from brahma.store import SettingDao

        with open(_TaskListener.__seo_file("sitemap.xml"), "w") as sitemap:
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
        logging.info("生成sitemap.xml完毕,开始压缩")

        with open(_TaskListener.__seo_file("sitemap.xml"), "rb") as f_in:
            with gzip.open(_TaskListener.__seo_file("sitemap.xml.gz"), "wb") as f_out:
                f_out.writelines(f_in)

        logging.info("压缩完毕")

    @staticmethod
    def rss():
        import importlib, tornado.options
        from brahma.store import SettingDao

        domain = SettingDao.get("site.domain")
        init = SettingDao.get("site.init")

        with open(_TaskListener.__seo_file("rss.xml"), "w") as rss:
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

        logging.info("生成rss.xml完毕")

    @staticmethod
    def qr():
        import qrcode
        from brahma.store import SettingDao

        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.ERROR_CORRECT_L,
            box_size=10,
            border=1,
        )

        qr.add_data("<a href='http://%s'>%s</a>" % (SettingDao.get("site.domain"), SettingDao.get("site.title")))
        qr.make(fit=True)

        img = qr.make_image()
        img.save(_TaskListener.__seo_file("site.png"))
        logging.info("生成site.png完毕")

    @staticmethod
    def __seo_file(name):
        import os
        from brahma.env import attach_dir

        d = "%s/seo" % attach_dir
        if not os.path.exists(d):
            os.makedirs(d)
        return d + "/" + name


    @staticmethod
    def email(to, title, body, html):
        from brahma.store import SettingDao

        smtp = SettingDao.get("site.smtp", encrypt=True)
        if smtp:
            import tornado.options
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
    logging.info("进程管理")
    from brahma.store import SettingDao

    SettingDao.startup(True)

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
            flag, args = _Queue.get()
            try:
                if flag == TaskFlag.EMAIL:
                    _TaskListener.email(**args)
                elif flag in [TaskFlag.QR, TaskFlag.ROBOTS, TaskFlag.SITEMAP, TaskFlag.RSS]:
                    import importlib

                    getattr(_TaskListener, flag)()
                elif flag == TaskFlag.ECHO:
                    _TaskListener.echo(args)
                else:
                    raise ValueError("丢弃任务[%s, %s]" % (flag, args))
            except Exception:
                logging.exception("执行任务出错")

    def _scanner():
        logging.info("启动定时扫描进程")
        import time, sched, tornado.options

        s = sched.scheduler(time.time, time.sleep)

        def run():
            import datetime
            from brahma.models import TaskFlag
            from brahma.store import TaskDao
            now = datetime.datetime.now()
            for t in TaskDao.list_available():
                if t.flag in [TaskFlag.QR, TaskFlag.ROBOTS, TaskFlag.SITEMAP, TaskFlag.RSS]:
                    TaskDao.set_next_run(t.id, datetime.datetime(year=now.year, month=now.month,day=now.day))
                    getattr(TaskSender, t.flag)()
                else:
                    logging.error("未知的任务类型[%s]" % t.flag)

        while True:
            s.enter(tornado.options.options.task_space, 10, run)
            s.run()

    _new_thread("task.listener", _listener)
    _new_thread("task.scanner", _scanner)


_init()