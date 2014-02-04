__author__ = 'zhengjitang@gmail.com'

import logging
import os
from urllib.parse import urlparse

from twisted.internet.protocol import Factory, connectionDone
from twisted.protocols.basic import LineReceiver
from twisted.internet import reactor


logger = logging.getLogger("server")


class _Protocol(LineReceiver):
    def __init__(self):
        logger.debug("初始化通信协议")

    def connectionMade(self):
        logger.debug("建立连接")

    def connectionLost(self, reason=connectionDone):
        logger.debug("关闭链接：%s" % reason.getErrorMessage())

    def lineReceived(self, line):
        logger.debug("请求：%s" % line)
        if line == b"quit":
            self.transport.loseConnection()
            return
        app, plugin, path = self.__parse(line)
        logger.debug("请求应用[%s]的")
        response = self.__process(line)
        logger.debug("响应：%s" % response)
        self.sendLine(response)

    def __parse(self, line):
        url = urlparse(line)
        return url.schema, url.netloc, url.path

    def __process(self, data):
        #todo
        return data


class _Factory(Factory):
    def __init__(self):
        #todo 参数设置
        pass

    def buildProtocol(self, addr):
        return _Protocol()


class Server:
    def __init__(self, port, store, mysql, redis, apps, debug=False):
        self.__port = port
        self.__store = store
        self.__debug = debug
        self.__apps = apps
        self.__mysql = mysql
        self.__redis = redis

        self.__check_dir("logs")
        self.__check_dir("cache")
        self.__init_logger()
        self.__init_store()

    def __init_logger(self):
        import logging.handlers

        logging.basicConfig()
        logger = logging.getLogger("server")
        logger.setLevel(logging.DEBUG if self.__debug else logging.INFO)
        _file_handler = logging.handlers.TimedRotatingFileHandler(self.__path("logs/daemon.log"), when='midnight')
        _file_handler.suffix = '-%Y%m%d.log'
        _file_handler.setFormatter(logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s'))

        logger.addHandler(_file_handler)

    def __init_store(self):
        for k in self.__apps:
            logger.info("检查应用[%s]" % k)
            for p in self.__apps[k]['plugins']:
                for d in ['seo', 'attach']:
                    self.__check_dir("%s/%s/%s" % (k, p, d))

    def __check_dir(self, d):
        d = self.__path(d)
        if not os.path.exists(d):
            os.makedirs(d)

    def __path(self, uri):
        return os.path.abspath(os.path.join(self.__store, uri))

    def start(self):
        logger.debug("启动")
        reactor.listenTCP(self.__port, _Factory())
        reactor.run()

