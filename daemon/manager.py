#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

import logging
import SocketServer
import commands
import os
import datetime
from protocols_pb2 import Request, Response
from package import Package

def initlog():
    logging.basicConfig(level=logging.DEBUG,
        format='%(asctime)s %(levelname)s %(message)s',
        filename='log', filemode='a+')
    logging.basicConfig()


class UDPHandler(SocketServer.BaseRequestHandler):
    __logger = logging.getLogger()
    __rv = {}

    def __named_start(self):
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, ['/etc/rc.d/named start', ])
        res.type = Response.SUCCESS
        return res

    def __named_stop(self):
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, ['/etc/rc.d/named stop', ])
        res.type = Response.SUCCESS
        return res

    def __named_status(self):
        res = Response()
        res.id = 0
        res.size = 1
        res.data.append(os.path.isfile('/var/run/named/named.pid') and u'正在运行' or u'尚未启动')
        res.type = Response.SUCCESS
        return res

    def __named_save(self, listen_ons, forwarders, controls, zones):
        named_conf = [
            'options {',
            'directory "/var/named";',
            'pid-file "/var/run/named/named.pid";',
            'datasize default;',
            'listen-on { %s;};' % listen_ons,
            'forward only;',
            'forwarders {\n%s;\n};' % forwarders,
            'allow-query { any; };',
            '};',
            'controls { inet %s port 953 allow { localhost; }; };' % controls,
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
            ]

        zone_list = {}
        for s in zones:
            zs = s.split(';')
            domain = zs[0]
            named_conf.append('zone "%s" IN {' % domain)
            named_conf.append('type master;')
            named_conf.append('file "%s.zone";' % domain)
            named_conf.append('allow-update {none;};')
            named_conf.append('};')

            zone_conf = [
                '$TTL 86400',
                '@	IN SOA	%s. ns.%s. (' % (domain, domain),
                '%s\n3H\n15M\n1W\n1D\n )' % datetime.datetime.now().strftime('%Y%m%d'),
                '\tIN NS	ns.%s.' % domain,
                ]
            mx_s = int(zs[1])
            a_s = int(zs[2])
            HEAD = 3
            for i in range(0, mx_s):
                zone_conf.append('\tIN MX %s %s.%s.' % (zs[HEAD + i * 2], zs[HEAD + i * 2 + 1], domain))

            for i in range(0, a_s):
                zone_conf.append('%s\tIN A\t%s' % (zs[HEAD + mx_s * 2 + i * 2], zs[HEAD + mx_s * 2 + i * 2 + 1]))

            zone_list[domain] = zone_conf

        fp = open('/etc/named.conf', 'w')
        for s in named_conf:
            fp.write(s)
            fp.write('\n')
        fp.close()

        for k, v in zone_list.items():
            fp = open('/var/named/%s.zone' % k, 'w')
            for s in v:
                fp.write(s)
                fp.write('\n')
            fp.close()

        res = Response()
        res.id = 0
        res.size = 1
        res.data.append(u'保存配置成功，正在重启服务')
        self.__command(res, ['/etc/rc.d/named restart', ])
        res.type = Response.SUCCESS
        return res


    def __dhcpd_start(self):
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, ['/etc/rc.d/dhcp4 start', ])
        res.type = Response.SUCCESS
        return res

    def __dhcpd_stop(self):
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, ['/etc/rc.d/dhcp4 stop', ])
        res.type = Response.SUCCESS
        return res

    def __dhcpd_status(self):
        res = Response()
        res.id = 0
        res.size = 1
        res.data.append(os.path.isfile('/var/run/dhcpd.pid') and u'正在运行' or u'尚未启动')
        res.type = Response.SUCCESS
        return res

    def __dhcpd_save(self, domain, lan_net, dns1, dns2, macs):
        conf_list = [
            'option domain-name "%s";' % domain,
            'default-lease-time 600;',
            'max-lease-time 7200;',
            'authoritative;',
            'log-facility local7;',
            'subnet %s.0 netmask 255.255.255.0 {' % lan_net,
            'range dynamic-bootp %s.2  %s.254;' % (lan_net, lan_net),
            'option broadcast-address %s.255;' % lan_net,
            'option routers %s.1;' % lan_net,
            'option domain-name-servers %s, %s;' % (dns1, dns2),
            '}',
            ]

        for s in macs:
            id, mac = s.split(';')
            conf_list.append('host %s-pc {' % id)
            conf_list.append('hardware ethernet %s;' % mac)
            conf_list.append('fixed-address %s.%s;' % (lan_net, id))
            conf_list.append('}')

        fp = open('/etc/dhcpd.conf', 'w')
        for s in conf_list:
            fp.write(s)
            fp.write('\n')
        fp.close()

        res = Response()
        res.id = 0
        res.size = 1
        res.data.append(u'保存配置成功，正在重启服务')
        self.__command(res, ['/etc/rc.d/dhcp4 restart', ])
        res.type = Response.SUCCESS
        return res

    def __tc_clear(self, wan_device, lan_device):
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, [
            'iptables -t mangle -F',
            'iptables -t mangle -X',
            'tc qdisc del dev %s root' % wan_device,
            'tc qdisc del dev %s root' % lan_device,
            ])
        res.type = Response.SUCCESS
        return res

    def __tc_status(self, wan_device, lan_device):
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, [
            'iptables -t mangle -n -L',
            'tc -s -d qdisc show dev %s' % wan_device,
            'tc -s -d class show dev %s' % wan_device,
            'tc -s -d qdisc show dev %s' % lan_device,
            'tc -s -d class show dev %s' % lan_device,
            ])
        res.type = Response.SUCCESS
        return res

    def __tc_apply(self, data):
        wan_device = data[0]
        lan_device = data[1]
        lan_net = data[2]
        cmd_list = [
            #清空打标信息
            'iptables -t mangle -F',
            'iptables -t mangle -X',
            #清空规则
            'tc qdisc del dev %s root 2>/dev/null' % wan_device,
            'tc qdisc del dev %s root 2>/dev/null' % lan_device,
            #顶层队列
            'tc qdisc add dev %s root handle 10: htb default 256' % wan_device,
            'tc qdisc add dev %s root handle 10: htb default 256' % lan_device,
            #第一层
            'tc class add dev %s parent 10: classid 10:1 htb rate 200mbit ceil 200mbit' % wan_device,
            'tc class add dev %s parent 10: classid 10:1 htb rate 500mbit ceil 500mbit' % lan_device,
            ]

        for s in data[3:]:
            id, up_rate, down_rate, up_ceil, down_ceil = s.split(';')
            #限速打标
            cmd_list.append('iptables -t mangle -A PREROUTING  -s %s.%s -j MARK --set-mark 2%s' % (lan_net, id, id))
            cmd_list.append('iptables -t mangle -A PREROUTING  -s %s.%s -j RETURN' % (lan_net, id))
            cmd_list.append('iptables -t mangle -A POSTROUTING -d %s.%s -j MARK --set-mark 2%s' % (lan_net, id, id))
            cmd_list.append('iptables -t mangle -A POSTROUTING -d %s.%s -j RETURN' % (lan_net, id))
            #限速规则
            cmd_list.append('tc class add dev %s parent 10:1 classid 10:2%s htb rate %skbps ceil %skbps prio 1' % (
                wan_device, id, up_rate, up_ceil))
            cmd_list.append('tc qdisc add dev %s parent 10:2%s handle 100%s: pfifo' % (wan_device, id, id))
            cmd_list.append('tc filter add dev %s parent 10: protocol ip prio 100 handle 2%s fw classid 10:2%s' % (
                wan_device, id, id))
            cmd_list.append('tc class add dev %s parent 10:1 classid 10:2%s htb rate %skbps ceil %skbps prio 1' % (
                lan_device, id, down_rate, down_ceil))
            cmd_list.append('tc qdisc add dev %s parent 10:2%s handle 100%s: pfifo' % (lan_device, id, id))
            cmd_list.append('tc filter add dev %s parent 10: protocol ip prio 100 handle 2%s fw classid 10:2%s' % (
                lan_device, id, id))

        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, cmd_list)
        res.type = Response.SUCCESS
        return res

    def __ff_status(self):
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, ['iptables -n -L', 'iptables -t nat -n -L'])
        res.type = Response.SUCCESS
        return res

    def __ff_apply(self, data):
        self.__ff_prepare()

        cmd_list = [
            'iptables -F',
            'iptables -X',
            'iptables -P INPUT DROP',
            'iptables -P OUTPUT ACCEPT',
            'iptables -P FORWARD DROP',
            'iptables -A INPUT -i lo -j ACCEPT',
            'iptables -A INPUT -m state --state RELATED -j ACCEPT',
            'iptables -A FORWARD  -m state --state RELATED -j ACCEPT',
            ]

        HEAD = 4
        wan_device = data[0]
        allow_ping = data[1]
        lan_device = data[2]
        lan_net = data[3]
        len_in, len_out, len_mac, len_nat = [int(i) for i in data[HEAD].split(';')]
        #print 'len(ins)=%s len(outs)=%s len(macs)=%s len(nats)=%s len(data)=%s' % \
        #      (len_in, len_out, len_mac, len_nat, len(data))

        #icmp设置
        icmps = [0, 3, '3/4', 4, 11, 12, 14, 16, 18]
        if allow_ping:
            icmps.append(8)
        for p in icmps:
            cmd_list.append('iptables -A INPUT -i %s -p icmp --icmp-type %s -j ACCEPT' % (wan_device, p))
            #入口规则
        for i in range(0, len_in):
            s_ip, protocol, d_ip, d_port = data[HEAD + i + 1].split(';')
            cmd_list.append(s_ip == '*'
                            and 'iptables -A INPUT -p %s -d %s --dport %s -j ACCEPT' % (protocol, d_ip, d_port)
            or 'iptables -A INPUT -s %s -p %s -d %s --dport %s -j ACCEPT' % (s_ip, protocol, d_ip, d_port))
        cmd_list.append('iptables -A INPUT -i %s -j ACCEPT' % lan_device)
        cmd_list.append('iptables -A INPUT -m state --state ESTABLISHED -j ACCEPT')

        #出口规则
        macs = {}
        for i in range(0, len_mac):
            #print data[HEAD + len_in + len_out + i + 1]
            id, mac = data[HEAD + len_in + len_out + i + 1].split(';')
            macs[id] = mac
        for i in range(0, len_out):
            #print data[HEAD + len_in + i + 1].split(';')
            out_list = data[HEAD + len_in + i + 1].split(';')
            domain, start, end, weeks = out_list[:4]
            for mac_id in out_list[4:]:
                cmd_list.append(
                    'iptables -A FORWARD  -m mac --mac-source %s -i %s -m time --kerneltz --timestart %s --timestop %s --weekdays %s -m string --string "%s" --algo bm -j ACCEPT' %
                    (macs[mac_id], lan_device, start, end, weeks, domain))
            cmd_list.append(
                'iptables -A FORWARD -i %s -m time --kerneltz --timestart %s --timestop %s --weekdays %s -m string --string "%s" --algo bm -j DROP' %
                (lan_device, start, end, weeks, domain))
        for id, mac in macs.items():
            cmd_list.append(
                'iptables -A FORWARD -m mac --mac-source %s -j ACCEPT' % mac)

        #NAT端口映射规则
        cmd_list.append('iptables -t nat -F')
        cmd_list.append('iptables -t nat -X')
        cmd_list.append('iptables -t nat -P PREROUTING ACCEPT')
        cmd_list.append('iptables -t nat -P POSTROUTING ACCEPT')
        cmd_list.append('iptables -t nat -P OUTPUT ACCEPT')

        for i in range(0, len_nat):
            s_ip, s_port, protocol, d_ip, d_port = data[HEAD + len_in + len_out + len_mac + i + 1].split(';')
            cmd_list.append('iptables -t nat -A PREROUTING -d %s -p %s --dport %s -j DNAT --to-destination %s:%s' % (
                s_ip, protocol, s_port, d_ip, d_port))
            cmd_list.append(
                'iptables -t nat -A POSTROUTING -s %s.0/24 -d %s -p %s --dport %s -j SNAT --to-source %s.1' % (
                    lan_net, d_ip, protocol, d_port, lan_net))
            cmd_list.append('iptables -A FORWARD -d %s -p %s --dport %s -j ACCEPT' % (d_ip, protocol, d_port))
        cmd_list.append('iptables -A FORWARD  -m state --state ESTABLISHED -j ACCEPT')
        cmd_list.append('iptables -t nat -A POSTROUTING -s %s.0/24 -o %s -j MASQUERADE' % (lan_net, wan_device))

        #测试代码
        if False:
            print '\n##############################################'
            for i in range(0, len(data)):
                print u'［%d］ %s' % (i, data[i].split(';') )
            print '\n'
            for cmd in cmd_list:
                print cmd
            print '\n##############################################'
            raise Exception()

        #返回
        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, cmd_list)
        res.type = Response.SUCCESS
        return res


    def __ff_clear(self, wan_device, lan_net):
        self.__ff_prepare()

        res = Response()
        res.id = 0
        res.size = 1
        self.__command(res, [
            'iptables -F',
            'iptables -X',
            'iptables -t nat -F',
            'iptables -t nat -X',
            'iptables -P INPUT ACCEPT',
            'iptables -P OUTPUT ACCEPT',
            'iptables -P FORWARD ACCEPT',
            'iptables -t nat -A POSTROUTING -o %s -s %s.0/24 -j MASQUERADE' % (wan_device, lan_net),
            ])
        res.type = Response.SUCCESS
        return res


    def __arp_sync(self, device):
        res = Response()
        res.id = 0
        res.size = 1
        errno, msgs = commands.getstatusoutput('arp -n -i %s' % device)
        for line in msgs.split('\n'):
            if line.find('ether') != -1:
                ip, type, mac, mask, eth = line.split()
                res.data.append('%s %s' % (mac, ip))
        res.type = Response.SUCCESS
        return res

    def __arp_scan(self):
        res = Response()
        res.id = 0
        res.size = 1

        self.__command(res, ['arp -n'])
        res.type = Response.SUCCESS
        return res

    def __command_(self, resp, cmds):
        for cmd in cmds:
            print cmd
        resp.data.append(u'操作成功')

    def __command(self, resp, cmds):
        for cmd in cmds:
            self.__logger.debug(cmd)
            err_no, msgs = commands.getstatusoutput(cmd)
            if err_no:
                #resp.data.append(u'错误码：%s' % err_no)
                resp.data.append(u'错误码：%s ［%s］' % (err_no, cmd))
            for line in msgs.split('\n'):
                if line.strip():
                    resp.data.append(line.decode('utf8'))
        if not resp.data:
            resp.data.append(u'操作成功')

    def __ff_prepare(self):
        cmd_list = []
        #加载内核需要的模块
        for mod in ['ip_tables', 'iptable_nat', 'ip_nat_ftp', 'ip_nat_irc',
                    'ip_conntrack', 'ip_conntrack_ftp', 'ip_conntrack_irc', 'ip_conntrack_netbios_ns']:
            cmd_list.append('modprobe -v %s' % mod)

        #设置核心网络功能
        fs = {
            '/proc/sys/net/ipv4/ip_forward': 1,
            '/proc/sys/net/ipv4/tcp_syncookies': 1,
            '/proc/sys/net/ipv4/icmp_echo_ignore_broadcasts': 1,
            }
        for dirpath, dirnames, filenames in os.walk('/proc/sys/net/ipv4/conf/'):
            if 'rp_filter' in filenames:
                fs[os.path.join(dirpath, 'rp_filter')] = 1
            if 'log_martians' in filenames:
                fs[os.path.join(dirpath, 'log_martians')] = 1
            if 'accept_source_route' in filenames:
                fs[os.path.join(dirpath, 'accept_source_route')] = 0
            if 'accept_redirects' in filenames:
                fs[os.path.join(dirpath, 'accept_redirects')] = 0
            if 'send_redirects' in filenames:
                fs[os.path.join(dirpath, 'send_redirects')] = 0
        for k in fs.keys():
            cmd_list.append('echo "%s" > %s' % (fs[k], k))

        for cmd in cmd_list:
            errno, msg = commands.getstatusoutput(cmd)
            self.__logger.debug(u"指令［%s］（%s, %s）" % (cmd, errno, msg.decode('utf-8')))


    def __log_request(self, request):
        self.__logger.debug(u"请求：［'%s', '%s', '%s', '%s', '%s'］" %
                            (self.client_address[0], request.type, request.id, request.size, ' '.join(request.data)))

    def __log_response(self, response):
        self.__logger.debug(u"回覆：［'%s', '%s', '%s', '%s'］" %
                            (response.type, response.id, response.size, ' '.join(response.data)))

    def __unknown(self):
        res = Response()
        res.id = 0
        res.size = 1
        res.type = Response.BAD_REQUEST
        return res

    def __print_rv(self):
        print '#############rv##############'
        for id, req in self.__rv.items():
            print req
        print '#############################'

    def __reply(self, resp):
        socket = self.request[1]
        self.__log_response(resp)
        socket.sendto(resp.SerializeToString(), self.client_address)
        import time
        time.sleep(0.01)

    def handle(self):
        data = self.request[0].strip()
        req = Request()
        req.ParseFromString(data)
        self.__log_request(req)

        if not Package.link(self.__rv, req):
            res = Response()
            res.id = 0
            res.size = 1
            res.type = Response.BAD_REQUEST
            self.__reply(res)
            return

        if not Package.is_full(self.__rv):
            res = Response()
            res.id = 0
            res.size = 1
            res.type = Response.SUCCESS
            self.__reply(res)
            return

        request = Package.assembly(self.__rv)

        response = {
            Request.DHCPD_START: lambda: self.__dhcpd_start(),
            Request.DHCPD_STOP: lambda: self.__dhcpd_stop(),
            Request.DHCPD_STATUS: lambda: self.__dhcpd_status(),
            Request.DHCPD_SAVE: lambda: self.__dhcpd_save(request.data[0], request.data[1], request.data[2],
                request.data[3], request.data[4:]),


            Request.NAMED_START: lambda: self.__named_start(),
            Request.NAMED_STOP: lambda: self.__named_stop(),
            Request.NAMED_STATUS: lambda: self.__named_status(),
            Request.NAMED_SAVE: lambda: self.__named_save(request.data[0], request.data[1], request.data[2],
                request.data[3:]),

            Request.TC_CLEAR: lambda: self.__tc_clear(request.data[0], request.data[1]),
            Request.TC_STATUS: lambda: self.__tc_status(request.data[0], request.data[1]),
            Request.TC_APPLY: lambda: self.__tc_apply(request.data),

            Request.FF_STATUS: lambda: self.__ff_status(),
            Request.FF_CLEAR: lambda: self.__ff_clear(request.data[0], request.data[1]),
            Request.FF_APPLY: lambda: self.__ff_apply(request.data),

            Request.ARP_SYNC: lambda: self.__arp_sync(request.data[0]),
            Request.ARP_SCAN: lambda: self.__arp_scan(),

            Request.UNKNOWN: lambda: self.__unknown(),
            }[request.type]()


        #self.__log_response(response)
        for resp in Package.split(response):
            #FF_APPLY0包老是丢，找到原因了 被自己的防火墙给拦截
            #FIXME 容易丢包 连发三遍 太土了
            for i in range(1, 3):
                self.__reply(resp)


if __name__ == '__main__':
    from optparse import OptionParser

    parser = OptionParser()
    parser.add_option('-n', '--host', dest='host', default='localhost', help=u'监听地址')
    parser.add_option('-d', '--daemon', dest='daemon', action='store_false', default=True, help=u'守护进程方式启动')
    (options, args) = parser.parse_args()

    initlog()
    logger = logging.getLogger()
    logger.debug(u'启动服务 监听地址 %s' % options.host)
    server = SocketServer.UDPServer((options.host, 9999), UDPHandler)
    try:
        server.serve_forever()
    except KeyboardInterrupt:
        logger.debug(u'停止服务')
    finally:
        server.server_close()
        logger.debug('bye')


  
