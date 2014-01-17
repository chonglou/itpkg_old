__author__ = 'zhengjitang@gmail.com'

import sys
import os
import time
import atexit
import signal


class Daemon:
    def __init__(self, pidfile):
        self.pidfile = pidfile

    def __daemonize(self):
        try:
            pid = os.fork()
            if pid > 0:
                sys.exit(0)
        except OSError as err:
            sys.stderr.write("创建进程#1失败:{0}\n".format(err))
            sys.exit(1)
        os.chdir("/")
        os.setsid()
        os.umask(0)

        try:
            pid = os.fork()
            if pid > 0:
                sys.exit(0)
        except OSError as err:
            sys.stderr.write("创建进程#2失败:{0}\n".format(err))
            sys.exit(1)
        sys.stdout.flush()
        sys.stderr.flush()
        si = open(os.devnull, "r")
        so = open(os.devnull, "a+")
        se = open(os.devnull, "a+")
        os.dup2(si.fileno(), sys.stdin.fileno())
        os.dup2(so.fileno(), sys.stdout.fileno())
        os.dup2(se.fileno(), sys.stderr.fileno())

        atexit.register(self.__delpid)

        pid = str(os.getpid())
        with open(self.pidfile, "w+") as f:
            f.write(pid + "\n")

    def __delpid(self):
        os.remove(self.pidfile)

    def start(self):
        try:
            with open(self.pidfile, "r") as pf:
                pid = int(pf.read().strip())
        except IOError:
            pid = None

        if pid:
            sys.stderr.write("pid文件[{0}]已经存在.\n".format(self.pidfile))
            sys.exit(1)

        self.__daemonize()
        self.run()

    def stop(self):
        try:
            with open(self.pidfile, "r") as pf:
                pid = int(pf.read().strip())
        except IOError:
            pid = None

        if not pid:
            sys.stderr.write("pid文件{0}不存在，没有运行？\n".format(self.pidfile))
            return

        try:
            while True:
                os.killpg(os.getpgid(pid), signal.SIGKILL)
                time.sleep(0.1)
        except OSError as err:
            e = str(err.args)
            if e.find("No such process") > 0:
                if os.path.exists(self.pidfile):
                    os.remove(self.pidfile)
            else:
                print(e)
                sys.exit(1)

    def restart(self):
        self.stop()
        time.sleep(0.1)
        self.start()

    def run(self):
        pass
