__author__ = 'zhengjitang@gmail.com'

import logging

import tornado.web
import tornado.options
import tornado.ioloop


class Server:
    def __init__(self, cfg, debug=False):
        self.__debug = debug
        self.__setup(cfg)
        self.__check_dirs()
        self.__init_logging()
        logging.debug("配置加载成功")

    def __setup(self, cfg):
        tornado.options.define("http_port", type=int, help="HTTP Server 监听端口")
        tornado.options.define("http_host", type=str, help="HTTP Server 监听地址")

        tornado.options.define("app_name", type=str, help="APP名称")
        tornado.options.define("app_secret", type=str, help="加密密钥")
        tornado.options.define("app_store", type=str, help="站点数据存储目录")
        tornado.options.define("app_plugins", type=list, help="插件列表", default=[])

        tornado.options.define("task_space", type=int, help="任务调度间隔", default=300)

        tornado.options.define("mysql_host", type=str, help="MySQL主机", default="localhost")
        tornado.options.define("mysql_port", type=int, help="MySQL端口", default=3306)
        tornado.options.define("mysql_user", type=str, help="MySQL用户名", default="root")
        tornado.options.define("mysql_password", type=str, help="MySQL登陆密码", default=None)
        tornado.options.define("mysql_pool_size", type=int, help="MySQL连接池大小", default=5)

        tornado.options.define("mysql_name", type=str, help="MySQL主机", default="localhost")
        tornado.options.define("redis_host", type=str, help="redis地址", default="localhost")
        tornado.options.define("redis_port", type=int, help="redis端口", default="6397")
        tornado.options.define("debug", type=bool, help="调试模式", default=self.__debug)

        tornado.options.options.log_to_stderr = self.__debug
        tornado.options.options.logging = "debug" if self.__debug else "info"
        tornado.options.parse_config_file(cfg)

        import logging

        logging.info("从[%s]加载配置" % cfg)

    def __init_logging(self):
        import logging.handlers

        logging.basicConfig()
        for name in ["sql", "redis"]:
            logger = logging.getLogger(name)
            logger.setLevel(logging.DEBUG if self.__debug else logging.INFO)
            _file_handler = logging.handlers.TimedRotatingFileHandler(
                "%s/logs/%s" % (tornado.options.options.app_store, name), when='midnight')
            _file_handler.suffix = '%Y%m%d'
            _file_handler.setFormatter(logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s'))
            logger.addHandler(_file_handler)

    def __check_dirs(self):
        import tornado.options
        import os, logging

        for d in ['logs', 'attach', 'cache']:
            d = "%s/%s" % (tornado.options.options.app_store, d)
            if not os.path.exists(d):
                os.makedirs(d)
                logging.debug("目录[%s]不存在，创建之" % d)

    def run(self):
        from brahma import jobs

        import tornado.options

        app = Application(tornado.options.options.debug)
        logging.info("开始监听端口%d", tornado.options.options.http_port)

        import socket
        from tornado.httpserver import HTTPServer
        from tornado.netutil import bind_sockets

        sockets = bind_sockets(
            port=tornado.options.options.http_port,
            address=tornado.options.options.http_host,
            family=socket.AF_INET)

        if not tornado.options.options.debug:
            import tornado.process

            tornado.process.fork_processes(0)

        server = HTTPServer(app, xheaders=True)
        server.add_sockets(sockets)

        if not self.__debug:
            def sig_handler(sig, frame):
                logging.warning("捕获信号%s" % sig)
                tornado.ioloop.IOLoop.instance().add_callback(shutdown)

            def shutdown():
                logging.info("停止服务")
                server.stop()
                timeout = 5
                logging.info("将在%d秒钟内关闭" % timeout)

                import time

                deadline = time.time() + timeout

                def stop():
                    loop = tornado.ioloop.IOLoop.instance()
                    now = time.time()
                    if now < deadline and (loop._callbacks or loop._timeouts):
                        loop.add_timeout(now + 1, stop)
                    else:
                        loop.stop()

                stop()

                #from brahma.store import Setting
                #Setting.startup(False)
                #from brahma.env import _db
                #_db.close()

            import signal

            signal.signal(signal.SIGTERM, sig_handler)
            signal.signal(signal.SIGINT, sig_handler)

        tornado.ioloop.IOLoop.instance().start()
        logging.info("退出服务")


class Application(tornado.web.Application):
    def __init__(self, debug):
        import tornado.options, importlib
        from brahma import utils, widgets
        from brahma.views import PageNotFoundHandler

        routes = []

        for i in utils.list_mod("brahma/views"):
            routes.extend(importlib.import_module("brahma.views." + i).handlers)

        for p in tornado.options.options.app_plugins:
            for i in utils.list_mod("brahma/plugins/" + p + "/views"):
                routes.extend(importlib.import_module("brahma.plugins." + p + ".views." + i).handlers)

        routes.append((r".*", PageNotFoundHandler), )

        import os

        settings = dict(
            ui_modules=widgets,
            template_path=os.path.realpath("templates"),
            static_path=os.path.realpath("statics"),
            login_url="/main",
            cookie_secret=self.__key(debug),
            xsrf_cookies=True,
            debug=debug,
        )

        tornado.web.Application.__init__(self, routes, **settings)

    def __key(self, debug):
        if debug:
            k = "debug"
        else:
            import base64, uuid

            k = base64.b64encode(uuid.uuid4().bytes + uuid.uuid4().bytes)
            logging.info("重新生成cookie key")
        return k







