__author__ = 'zhengjitang@gmail.com'

import importlib
import logging
import tornado.ioloop
import tornado.web

from brahma.daemon import Daemon


class Application(tornado.web.Application):
    def __init__(self, debug):
        import tornado.options
        from brahma import utils, widgets
        from brahma.views import PageNotFoundHandler

        routes = []

        for i in utils.list_mod("../views"):
            routes.extend(importlib.import_module("brahma.views." + i).handlers)

        for p in tornado.options.options.app_plugins:
            for i in utils.list_mod("../plugins/" + p + "/views"):
                routes.extend(importlib.import_module("brahma.plugins." + p + ".views." + i).handlers)

        routes.append((r".*", PageNotFoundHandler))

        settings = dict(
            ui_modules=widgets,
            template_path=utils.path("../../templates"),
            static_path=utils.path("../../statics"),
            login_url="/personal/login",
            cookie_secret=tornado.options.options.http_secret,
            xsrf_cookies=True,
            debug=debug,
        )

        tornado.web.Application.__init__(self, routes, **settings)


class HttpDaemon(Daemon):
    def run(self, debug=False):
        self.__setup(debug)

        import tornado.options

        app = Application(debug)
        logging.info("开始监听端口%d", tornado.options.options.http_port)

        #app.listen(
        #    port=tornado.options.options.http_port,
        #    address=tornado.options.options.http_host)
        import socket
        from tornado.httpserver import HTTPServer
        from tornado.netutil import bind_sockets

        sockets = bind_sockets(
            port=tornado.options.options.http_port,
            address=tornado.options.options.http_host,
            family=socket.AF_INET)
        server = HTTPServer(app, xheaders=True)
        server.add_sockets(sockets)
        if not debug:
            import tornado.process

            tornado.process.fork_processes(0)
        tornado.ioloop.IOLoop.instance().start()


    def __setup(self, debug):
        from brahma import utils
        import tornado.options

        app_store = utils.path("../../tmp")

        tornado.options.define("http_secret", type=str, help="COOKIE密钥")
        tornado.options.define("http_port", type=int, help="HTTP Server 监听端口")
        tornado.options.define("http_host", type=str, help="HTTP Server 监听地址")
        tornado.options.define("app_name", type=str, help="APP名称")
        tornado.options.define("app_secret", type=str, help="加密密钥")
        tornado.options.define("app_store", type=str, help="站点数据存储目录", default=app_store)
        tornado.options.define("app_plugins", type=list, help="插件列表", default=[])

        tornado.options.define("db_uri", type=str, help="数据库连接", default="sqlite:///" + app_store + "/db")
        tornado.options.define("redis_host", type=str, help="redis地址")
        tornado.options.define("redis_port", type=int, help="redis端口")

        tornado.options.define("debug", type=bool, help="redis端口", default=debug)

        tornado.options.options.log_file_prefix = tornado.options.options.app_store + "/httpd.log"
        tornado.options.parse_config_file(utils.path("../../web.cfg"))








