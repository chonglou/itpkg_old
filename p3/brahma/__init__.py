__author__ = 'zhengjitang@gmail.com'

import tornado.web
import tornado.options
import tornado.ioloop


class Server:
    def __init__(self, cfg, debug=False):
        self.__debug = debug
        self.__setup(cfg)
        self.__check_dirs()
        self.__init_logger()
        logger.debug("配置加载成功")

    def __setup(self, cfg):
        tornado.options.define("http_port", type=int, help="HTTP Server 监听端口")
        tornado.options.define("http_host", type=str, help="HTTP Server 监听地址")

        tornado.options.define("app_name", type=str, help="APP名称")
        tornado.options.define("app_secret", type=str, help="加密密钥")
        tornado.options.define("app_store", type=str, help="站点数据存储目录")
        tornado.options.define("app_plugins", type=list, help="插件列表", default=[])
        tornado.options.define("task_space", type=int, help="任务调度间隔", default=300)
        tornado.options.define("mysql", type=str, help="MySQL连接", default="root@localhost/brahma")
        tornado.options.define("redis_host", type=str, help="redis地址", default="localhost")
        tornado.options.define("redis_port", type=int, help="redis端口", default="6397")
        tornado.options.define("debug", type=bool, help="调试模式", default=self.__debug)

        tornado.options.options.log_to_stderr = self.__debug
        tornado.options.options.logging = "debug" if self.__debug else "info"
        tornado.options.parse_config_file(cfg)

        import logging

        logging.info("从[%s]加载配置" % cfg)

    def __init_logger(self):
        global logger
        import logging.handlers

        logging.basicConfig()
        logger = logging.getLogger(tornado.options.options.app_name)
        logger.setLevel(logging.DEBUG if self.__debug else logging.INFO)
        _file_handler = logging.handlers.TimedRotatingFileHandler(
            tornado.options.options.app_store + "/logs/daemon.log", when='midnight')
        _file_handler.suffix = '-%Y%m%d.log'
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
        import tornado.options

        app = Application(tornado.options.options.debug)
        logger.info("开始监听端口%d", tornado.options.options.http_port)

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

        def sig_handler(sig, frame):
            logger.warning("捕获信号%s" % sig)
            tornado.ioloop.IOLoop.instance().add_callback(shutdown)

        def shutdown():
            logger.info("停止服务")
            server.stop()
            tornado.ioloop.IOLoop.instance().stop()

        import signal
        signal.signal(signal.SIGTERM, sig_handler)
        signal.signal(signal.SIGINT, sig_handler)

        tornado.ioloop.IOLoop.instance().start()
        logger.info("退出服务")

class Application(tornado.web.Application):
    def __init__(self, debug):
        import tornado.options, importlib
        from brahma import utils, widgets
        from brahma.views import PageNotFoundHandler

        routes = []

        #for i in utils.list_mod("../views"):
        #    routes.extend(importlib.import_module("brahma.views." + i).handlers)

        #for p in tornado.options.options.app_plugins:
        #    for i in utils.list_mod("../plugins/" + p + "/views"):
        #        routes.extend(importlib.import_module("brahma.plugins." + p + ".views." + i).handlers)

        routes.append((r".*", PageNotFoundHandler))

        settings = dict(
            ui_modules=widgets,
            template_path=utils.path("../../templates"),
            static_path=utils.path("../../statics"),
            login_url="/main",
            cookie_secret=self.__key(),
            xsrf_cookies=True,
            debug=debug,
        )

        tornado.web.Application.__init__(self, routes, **settings)

    def __key(self):
        import base64,uuid
        k = base64.b64encode(uuid.uuid4().bytes + uuid.uuid4().bytes)
        logger.debug("生成cookie key:%s" % k)
        return k


logger = None






