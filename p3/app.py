__author__ = 'zhengjitang@gmail.com'

if __name__ == "__main__":
    import sys, os

    if len(sys.argv) == 3 and os.path.exists(sys.argv[1]) and sys.argv[2] in ['debug', 'start', 'stop', 'restart']:
        import configparser

        config = configparser.ConfigParser()
        config.read(sys.argv[1])
        if config.getboolean('global', 'server'):
            from brahma import ServerDaemon

            cfg = {}

            for k in ['mysql', 'redis', 'store']:
                cfg[k] = config['global'][k]
            cfg['port'] = config.getint("global", 'port')
            apps = {}
            for k in config['global']['apps'].split(','):
                app = {"plugins": config[k]['plugins'].split(','), 'key': config[k]['key']}
                apps[k] = app
            cfg['apps'] = apps

            daemon = ServerDaemon(**cfg)

        else:
            from brahma import AppDaemon

            cfg = {}

            for k in ['host', 'port', 'name']:
                cfg[k] = config['global'][k]

            daemon = AppDaemon(**cfg)

        getattr(daemon, sys.argv[2])()

    else:
        print("正确用法: python {0} xxx.cfg debug|start|stop|restart".format(sys.argv[0]))
        print("配置文件xxx.cfg需存在")

