__author__ = 'zhengjitang@gmail.com'

import logging


class Rpc:
    def __init__(self, host, user="root", password=None, port=22, flag="ArchLinux"):
        self.__host = host
        self.__user = user
        self.__password = password
        self.__port = port
        self.__flag = flag


    def call(self, commands):
        result = list()
        ok = False
        try:
            from pexpect import pxssh

            ssh = pxssh.pxssh()
            ssh.login(server=self.__host, username=self.__user, password=self.__password, port=self.__port)
            for cmd in commands:
                logging.debug(cmd)
                ssh.sendline(cmd)
                ssh.prompt()
                out = ssh.before.decode().split('\n')
                logging.debug(out)
                result.extend(out)
            ssh.logout()
            ok = True
        except:
            logging.exception("运行命令出错")
        return ok, result


class ArchLinux:
    @staticmethod
    def reboot():
        return "reboot"

    @staticmethod
    def uname():
        return "uname -a"

    @staticmethod
    def password(password):
        return 'echo "root:%s" | chpasswd' % password

    @staticmethod
    def hostname(name):
        return "hostnamectl set-hostname %s" % name

    @staticmethod
    def udev(wanMac, lanMac):
        return ArchLinux.file("/etc/udev/rules.d/10-network.rules", [
            'SUBSYSTEM=="net", ACTION=="add", ATTR{address}=="%s", NAME="wan"' % wanMac,

            'SUBSYSTEM=="net", ACTION=="add", ATTR{address}=="%s", NAME="lan"' % lanMac,
        ])

    @staticmethod
    def lan(net):
        return ArchLinux.file(
            "/etc/netctl/lan",
            [
                "Description='lan'",
                "Connection=ethernet",
                "Interface=lan",
                "IP=static",
                "Address=('%s.1/24')" % net,
            ]
        )

    @staticmethod
    def wan_dhcp(dns1, dns2):
        return ArchLinux.file("/etc/netctl/wan", [
            "Description='wan'",
            "Connection=ethernet",
            "Interface=wan",
            "IP=dhcp",
            "DNS=('%s' '%s')" % (dns1, dns2),
        ])

    @staticmethod
    def wan_static(ip, netmask, gateway, dns1, dns2):
        return ArchLinux.file("/etc/netctl/wan", [
            "Description='wan'",
            "Connection=ethernet",
            "Interface=wan",
            "IP=static",
            "Address=('%s/%s')" % (ip, netmask),
            "Gateway='%s'" % gateway,
            "DNS=('%s' '%s')" % (dns1, dns2),
        ])

    @staticmethod
    def file(name, lines):
        return "cat << EOF > %s \n%s\nEOF" % (name, '\n'.join(lines))
