__author__ = 'zhengjitang@gmail.com'

import logging


class ArchLinux:
    @staticmethod
    def scan():
        return "arp -e -i lan"
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


class Rpc:
    def __init__(self, host, user="root", password=None, port=22, flag="ArchLinux"):
        self.__host = host
        self.__user = user
        self.__password = password
        self.__port = port
        self.__flag = flag


    def set_wan(self, ip, netmask, gateway, dns1, dns2):
        if self.__flag == "ArchLinux":
            return self.call([
                ArchLinux.wan_static(ip, netmask, gateway, dns1, dns2),
                #"netctl restart wan",
            ])
        return self.__fail()

    def set_lan(self, net):
        if self.__flag == "ArchLinux":
            return self.call([
                ArchLinux.lan(net),
                "netctl restart lan",
            ])
        return self.__fail()

    @staticmethod
    def __fail():
        return False,["尚未不支持"]

    def call(self, commands):
        result = list()
        ok = False
        try:
            from pexpect import pxssh

            ssh = pxssh.pxssh()
            logging.debug("登录:%s@%s:%s"%(self.__user, self.__host, self.__port))
            ssh.login(server=self.__host, username=self.__user, password=self.__password, port=self.__port)
            for cmd in commands:
                logging.debug("命令\n%s"%cmd)
                ssh.sendline(cmd)
                ssh.prompt()
                out = ssh.before.decode().split('\n')
                logging.debug("结果\n%s"%out)
                result.extend(out)
            ssh.logout()
            ok = True
        except:
            logging.exception("运行命令出错")
            result.append("网络通讯出错")
        return ok, result

    def scan(self):
        if self.__flag == "ArchLinux":
            ok,result = self.call([ArchLinux.scan()])
            if ok:
                val = list()
                import shlex
                for i in range(1, len(result)):
                    ss = shlex.split(result[i])
                    if len(ss) == 5:
                        ip = ss[1]
                        val.append((ss[2], ip[ip.rindex('.')+1:]))
                return ok, val
            else:
                return ok, result

        return self.__fail()







def create(rid):
    import json
    from brahma.plugins.itpkg.store import RouterDao
    r = RouterDao.get(rid)
    wan = json.loads(r.wan)
    return Rpc(host=wan['ip'], flag=r.flag)
