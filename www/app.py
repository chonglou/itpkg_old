__author__ = 'zhengjitang@gmail.com'

if __name__ == "__main__":
    import sys
    from brahma import HttpDaemon, utils

    daemon = HttpDaemon(utils.path("../../tmp/pid"))
    if len(sys.argv) == 2:
        if "start" == sys.argv[1]:
            daemon.start()
        elif "stop" == sys.argv[1]:
            daemon.stop()
        elif "restart" == sys.argv[1]:
            daemon.restart()
        elif "debug" == sys.argv[1]:
            daemon.run(True)
        else:
            print("未知参数：[{0}]".format(sys.argv[1]))
            sys.exit(2)
        sys.exit(0)
    else:
        print("正确用法: python {0} debug|start|stop|restart".format(sys.argv[0]))
        sys.exit(2)