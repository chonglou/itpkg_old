__author__ = 'zhengjitang@gmail.com'

import logging

from brahma.plugins.itpkg.models import RouterFlag


class ArchLinux:
    @staticmethod
    def firewall_empty():
        return [
            "iptables -F",
            "iptables -X",
            "iptables -t nat -F",
            "iptables -t nat -X",
            "iptables -t mangle -F",
            "iptables -t mangle -X",
            "iptables -t raw -F",
            "iptables -t raw -X",
            "iptables -t security -F",
            "iptables -t security -X",
            "iptables -P INPUT ACCEPT",
            "iptables -P FORWARD ACCEPT",
            "iptables -P OUTPUT ACCEPT",
        ]

    @staticmethod
    def firewall_status():
        return [
            "iptables -nvL --line-numbers",
            "iptables -nvL --line-numbers -t nat"
        ]

    @staticmethod
    def firewall_save():
        return ["iptables-save > /etc/iptables/iptables.rules"]

    @staticmethod
    def firewall_apply(net, ins=list(), nats=list(), outs=list(), macs=list()):
        #基本规则
        rules = [
            ArchLinux.firewall_empty(),
            "iptables -N TCP",
            "iptables -N UDP",
            "iptables -P OUTPUT ACCEPT",
            "iptables -P INPUT DROP",
            "iptables -A INPUT -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT",
            "iptables -A INPUT -i lo -j ACCEPT",
            "iptables -A INPUT -m conntrack --ctstate INVALID -j DROP",
            "iptables -A INPUT -p icmp --icmp-type 8 -m conntrack --ctstate NEW -j ACCEPT",
            "iptables -A INPUT -p udp -m conntrack --ctstate NEW -j UDP",
            "iptables -A INPUT -p tcp --syn -m conntrack --ctstate NEW -j TCP",
            "iptables -A INPUT -p udp -j REJECT --reject-with icmp-port-unreachable",
            "iptables -A INPUT -p tcp -j REJECT --reject-with tcp-rst",
            "iptables -A INPUT -j REJECT --reject-with icmp-proto-unreachable",
            "iptables -A TCP -p tcp -m tcp --dport 22 -j ACCEPT",
        ]

        #入口规则
        for protocol, port in ins:
            if protocol == "tcp":
                rules.append("iptables -A TCP -p tcp -m tcp --dport %s -j ACCEPT" % port)
            elif protocol == "udp":
                rules.append("iptables -A UDP -p udp -m udp --dport %s -j ACCEPT" % port)

        #转发规则
        rules.extend([
            "echo 1 > /proc/sys/net/ipv4/ip_forward",
            "iptables -N fw-interfaces",
            "iptables -N fw-open",
            "iptables -A FORWARD -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT",
            "iptables -A FORWARD -j fw-interfaces",
            "iptables -A FORWARD -j fw-open",
            "iptables -A FORWARD -j REJECT --reject-with icmp-host-unreach",
            "iptables -P FORWARD DROP",
        ])

        # 出口规则
        #"iptables -A fw-interfaces -i lan -j ACCEPT",
        for d, s, e, w, ms in outs:
            for m in ms:
                rules.append(
                    'iptables -A fw-interfaces -m mac --mac-source %s -i lan -m time --kerneltz --timestrart %s --timestop %s --weekdays %s -m string --string "%s" --algo bm -j ACCEPT' %
                    (m, s, e, w, d)
                )
            rules.append(
                'iptables -A fw-interfaces -i lan -m time --kerneltz --timestrart %s --timestop %s --weekdays %s -m string --string "%s" --algo bm -j ACCEPT' %
                (s, e, w, d)
            )

        for m in macs:
            rules.append("iptables -A fw-interfaces -m mac --mac-source %s -j ACCEPT" % m)

        rules.append("iptables -t nat -A POSTROUTING -s %s.0/24 -o wan -j MASQUERADE" % net)

        # nat 规则
        for sport, protocol, dip, dport in nats:
            rules.extend([
                "iptables -A fw-open -d %s.%s -p %s --dport %s -j ACCEPT" % (net, dip, protocol, dport),
                "iptables -t nat -A PREROUTING -i wan -p %s --dport %s -j DNAT --to %s.%s:%s" % (
                    protocol, sport, net, dip, dport),
            ])
        return rules

    @staticmethod
    def _sh_name():
        import uuid

        return "/root/.%s.sh" % uuid.uuid4().hex

    @staticmethod
    def firewall_clear(net):
        sh = ArchLinux._sh_name()
        rules = ArchLinux.file(sh, [
            "#!/bin/sh",
            "iptables -F",
            "iptables -X",
            "iptables -t nat -F",
            "iptables -t nat -X",
            "iptables -t mangle -F",
            "iptables -t mangle -X",
            "iptables -t raw -F",
            "iptables -t raw -X",
            "iptables -t security -F",
            "iptables -t security -X",
            "iptables -P INPUT DROP",
            "iptables -P OUTPUT ACCEPT",
            "iptables -P FORWARD DROP",
            "iptables -N TCP",
            "iptables -N UDP",
            "iptables -A INPUT -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT",
            "iptables -A INPUT -i lo -j ACCEPT",
            "iptables -A INPUT -i lan -j ACCEPT",
            "iptables -A INPUT -m conntrack --ctstate INVALID -j DROP",
            "iptables -A INPUT -p icmp --icmp-type 8 -m conntrack --ctstate NEW -j ACCEPT",
            "iptables -A INPUT -p udp -m conntrack --ctstate NEW -j UDP",
            "iptables -A INPUT -p tcp --tcp-flags FIN,SYN,RST,ACK SYN -m conntrack --ctstate NEW -j TCP",
            "iptables -A INPUT -p udp -j REJECT --reject-with icmp-port-unreachable",
            "iptables -A INPUT -p tcp -j REJECT --reject-with tcp-rst",
            "iptables -A INPUT -j REJECT --reject-with icmp-proto-unreachable",
            "iptables -A TCP -p tcp --dport 22 -j ACCEPT",
            "echo 1 > /proc/sys/net/ipv4/ip_forward",
            "for f in /proc/sys/net/ipv4/conf/*/rp_filter ; do echo 1 > $f ; done",
            "iptables -N fw-interfaces",
            "iptables -N fw-open",
            "iptables -A FORWARD -m conntrack --ctstate RELATED,ESTABLISHED -j ACCEPT",
            "iptables -A FORWARD -j fw-interfaces",
            "iptables -A FORWARD -j fw-open",
            "iptables -A FORWARD -j REJECT --reject-with icmp-host-unreach",
            "iptables -P FORWARD DROP",
            "iptables -A fw-interfaces -i lan -j ACCEPT",
            "iptables -t nat -A POSTROUTING -s %s.0/24 -o wan -j MASQUERADE" % net,
        ])
        rules.append("sh %s" % sh)
        return rules

    @staticmethod
    def named(net, dns1, dns2, zones=None):
        named_conf = list()
        named_conf.extend([
            'options {',
            'directory "/var/named";',
            'pid-file "/var/run/named/named.pid";',
            'datasize default;',
            'listen-on { %s.1;};' % net,
            'forward only;',
            'forwarders {\n%s;%s;\n};' % (dns1, dns2),
            'allow-query { any; };',
            '};',
            'controls { inet %s.1 port 953 allow { localhost; }; };' % net,
            'zone "localhost" IN {',
            'type master;',
            'file "localhost.zone";',
            'allow-transfer { any; };',
            '};',
            'zone "0.0.127.in-addr.arpa" IN {',
            'type master;',
            'file "127.0.0.zone";',
            'allow-transfer { any; };',
            '};',
            'zone "." IN {',
            'type hint;',
            'file "root.hint";',
            '};',
            'logging {',
            'channel xfer-log {',
            'file "/var/log/named.log";',
            'print-category yes;',
            'print-severity yes;',
            'print-time yes;',
            'severity info;',
            '};',
            'category xfer-in { xfer-log; };',
            'category xfer-out { xfer-log; };',
            'category notify { xfer-log; };',
            '};',
        ])
        #todo 域名解析
        return ArchLinux.file("/etc/named.conf", named_conf, mode="440", own="root:named")


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
    def dhcpd(domain, net, items):
        lines = list()
        lines.extend([
            'option domain-name "%s";' % domain,
            'default-lease-time 600;',
            'max-lease-time 7200;',
            'authoritative;',
            'log-facility local7;',
            'subnet %s.0 netmask 255.255.255.0 {' % net,
            'range dynamic-bootp %s.2  %s.254;' % (net, net),
            'option broadcast-address %s.255;' % net,
            'option routers %s.1;' % net,
            'option domain-name-servers %s.1, %s.1;' % (net, net),
            '}'
        ])
        for mac, ip in items:
            lines.extend([
                'host %s-pc {' % ip,
                'hardware ethernet %s;' % mac,
                'fixed-address %s.%s;' % (net, ip),
                '}'
            ])

        return ArchLinux.file("/etc/dhcpd.conf", lines)

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
    def daemon(name, action, old):
        if action == "status" and old:
            if name == "dhcp4":
                return "ps auwx | grep dhcpcd"
            elif name == "named":
                return "ps awux | grep named"
        return "/etc/rc.d/%s %s" % (name, action) if old else "systemctl %s %s" % (action, name)

    @staticmethod
    def status(old):
        cmds = ["uname -a", "uptime", "free -m"]
        if not old:
            cmds.append("iostat")
        return cmds

    @staticmethod
    def file(name, lines, mode="400", own="root:root"):
        return [
            "cat << EOF > %s \n%s\nEOF" % (name, '\n'.join(lines)),
            "chmod %s %s" % (mode, name),
            "chown %s %s" % (own, name)
        ]


class Rpc:
    def __init__(self, flag, host, user="root", password=None, port=22):
        self.__host = host
        self.__user = user
        self.__password = password
        self.__port = port
        self.__flag = flag

    def status(self):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD:
            return self.call(ArchLinux.status(True))
        elif self.__flag == RouterFlag.ARCH_LINUX_NEW:
            return self.call(ArchLinux.status(False))
        return self.__fail()

    def status_firewall(self):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD or self.__flag == RouterFlag.ARCH_LINUX_NEW:
            return self.call(ArchLinux.firewall_status())
        return self.__fail()

    def apply_firewall(self, net, ins, outs, nats, macs):
        if self.__is_archLinux():
            cmds = ArchLinux.firewall_apply(net=net, ins=ins, outs=outs, nats=nats, macs=macs)
            cmds.append(ArchLinux.firewall_save())
            return self.call(cmds)
        return self.__fail()

    def clear_firewall(self, net):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD or RouterFlag.ARCH_LINUX_NEW():
            cmds = ArchLinux.firewall_clear(net)
            return self.call(cmds)
        return self.__fail()

    def apply_named(self, net, dns1, dns2):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD:
            cmds = ArchLinux.named(net, dns1, dns2)
            cmds.append(ArchLinux.daemon("named", "restart", True))
            return self.call(cmds)
        return self.__fail()

    def status_named(self):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD:
            return self.call([ArchLinux.daemon("named", "status", True)])
        return self.__fail()

    def status_dhcpd(self):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD:
            return self.call([ArchLinux.daemon("dhcp4", "status", True)])
        return self.__fail()

    def apply_dhcpd(self, domain, net, items):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD:
            cmds = ArchLinux.dhcpd(domain, net, items)
            cmds.append(ArchLinux.daemon("dhcp4", "restart", True))
            return self.call(cmds)
        return self.__fail()

    def set_wan(self, ip, netmask, gateway, dns1, dns2):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD:
            return self.__un_support()
        elif self.__flag == RouterFlag.ARCH_LINUX_NEW:
            return self.call(ArchLinux.wan_static(ip, netmask, gateway, dns1, dns2))
        return self.__fail()

    def __un_support(self):
        logging.error("暂不支持")
        return True, []

    def set_lan(self, net):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD:
            return self.__un_support()
        elif self.__flag == RouterFlag.ARCH_LINUX_NEW:
            cmds = ArchLinux.lan(net)
            cmds.append("netctl restart lan")
            return self.call(cmds)
        return self.__fail()


    def scan(self):
        if self.__flag == RouterFlag.ARCH_LINUX_OLD or self.__flag == RouterFlag.ARCH_LINUX_NEW:
            ok, result = self.call([ArchLinux.scan()])
            if ok:
                val = list()
                import shlex

                for i in range(2, len(result)):
                    ss = shlex.split(result[i])
                    if len(ss) == 5:
                        ip = ss[0]
                        val.append((ss[2], int(ip[ip.rindex('.') + 1:])))
                return ok, val
            else:
                return ok, result

        return self.__fail()

    @staticmethod
    def __fail():
        return False, ["尚未不支持"]

    def call(self, commands):
        result = list()
        ok = False
        try:
            from pexpect import pxssh

            ssh = pxssh.pxssh()
            logging.debug("登录:%s@%s:%s" % (self.__user, self.__host, self.__port))
            logging.debug("请求：\n%s", "\n".join(commands))
            ssh.login(server=self.__host, username=self.__user, password=self.__password, port=self.__port)
            for cmd in commands:
                logging.debug("运行\n%s" % cmd)
                ssh.sendline(cmd)
                ssh.prompt()
                out = ssh.before.decode().split('\n')
                logging.debug("结果\n%s" % out)
                result.extend(out)
            ssh.logout()
            ok = True
        except:
            logging.exception("运行命令出错")
            result.append("网络通讯出错")
        return ok, result


def create(rid):
    from brahma.plugins.itpkg.store import RouterDao

    wan, flag = RouterDao.get_wan_flag(rid)
    return Rpc(host=wan.ip, flag=flag)
