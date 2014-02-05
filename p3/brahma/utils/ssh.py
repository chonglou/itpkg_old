__author__ = 'zhengjitang@gmail.com'

import logging


class Ssh:
    def __init__(self):
        self.__hosts = dict()

    def login(self, host, user="root", password=None, port=22):
        try:
            import uuid
            from pexpect import pxssh

            ssh = pxssh.pxssh()
            #vt100 dumb
            ssh.login(server=host, username=user, port=port, password=password)
            sid = uuid.uuid4().hex
            self.__hosts[sid] = ssh
            return sid
        except Exception:
            logging.exception("SSH登录出错")
        return None

    def logout(self, sid):
        if sid in self.__hosts:
            ssh = self.__hosts.pop(sid)
            try:
                ssh.logout()
            except Exception:
                logging.exception("SSH退出出错")

    def execute(self, sid, command):
        msg = list()
        if sid in self.__hosts:
            ssh = self.__hosts[sid]
            try:
                ssh.sendline(command)
                ssh.prompt()
                return ssh.before.decode().split('\n')
            except Exception:
                logging.exception("运行命令出错")
                msg.append("运行命令出错")
        else:
            msg.append("尚未登录")
        return msg




