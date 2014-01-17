__author__ = 'zhengjitang@gmail.com'

import fcntl
import os
import select
from subprocess import Popen, PIPE


class Ssh:
    def __init__(self, host, port=22, user="root", shell="/bin/sh", ssh="/usr/bin/ssh"):
        self.user = user
        self.host = host
        self.port = port
        self.shell = shell
        self.ssh = ssh
        self.is_start = False
        self.is_stop = False
        self.code = None


    def _start_ssh_process(self):
        self.proc = Popen(
            [self.ssh, "{0}@{1}:{2}".format(self.user, self.host, self.port), self.shell],
            stdin=PIPE,
            stdout=PIPE,
            stderr=PIPE,
        )
        pool_result = self.proc.poll()
        if pool_result:
            self.code = pool_result
            return self.stderr()
        self.is_start = True
        return None

    def _read(self, file):
        output = []
        while True:
            r, w, e = select.select([file], [], [], 0.2)
            if len(r) == 0:
                break
            data = self._non_block_read(r[0])
            if not data:
                break
            output.append(data)
        return b"".join(output)

    def write(self, data):
        if isinstance(data, str):
            data = bytes(data)
            num = self.proc.stdin.write(data)
            self.proc.stdin.flush()
            return num

    def stdout(self):
        return self._read(self.proc.stdout)

    def stderr(self):
        return self._read(self.proc.stderr)

    def start(self):
        if not self.is_start:
            self._start_ssh_process()

    def stop(self):
        if self.is_start:
            self.proc.terminate()

    def execute(self, command):
        if isinstance(command, str):
            command = bytes(command)
            if command[-1] != b"\n":
                command += b"\n"
            n = self.write(command)
            result, code, *o = self.stdout().rsplit(b"\n", 2)
            return int(code), result


    def _non_block_read(self, output):
        fd = output.fileno()
        fl = fcntl.fcntl(fd, fcntl.F_GETFL)
        fcntl.fcntl(fd, fcntl.F_SETFL, fl | os.O_NONBLOCK)
        try:
            return output.read()
        except:
            return None





