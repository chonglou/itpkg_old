__author__ = 'zhengjitang@gmail.com'

import configparser
from brahma import daemon,server


class ServerDaemon(daemon.Daemon):
    def __init__(self, **kwargs):
        super().__init__("%s/server.pid"%kwargs["store"])
        self.__args = kwargs

    def run(self, debug=False):
        from brahma.server import Server
        self.__server = Server(debug=debug, **self.__args)
        self.__server.start()


class AppDaemon(daemon.Daemon):
    def __init__(self, name, host, port):
        print("%s@%s:%s" % (name, host, port))
        super().__init__("/tmp/app.%s.pid" % name)

    def run(self, debug=False):
        pass
