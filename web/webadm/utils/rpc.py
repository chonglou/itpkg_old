#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

import socket
import time
from protocols_pb2 import Request, Response

from webadm.utils.package import Package

class Rpc(object):
    TIMEOUT = 10

    def __init__(self, host):
        self.host = (host, 9999)


    def named_start(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.NAMED_START
        res = self.__request(req)
        return res.data

    def named_stop(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.NAMED_STOP
        res = self.__request(req)
        return res.data

    def named_status(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.NAMED_STATUS
        res = self.__request(req)
        return res.data

    def named_save(self, listen_ons, forwarders, controls, zones):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.NAMED_SAVE
        req.data.append(listen_ons)
        req.data.append(forwarders)
        req.data.append(controls)

        for z in zones:
            if z.ns_items:
                mx_s=[]
                a_s = []

                for item in z.ns_items:
                    if item.mx_priority:
                        mx_s.append('%s;%s' % (item.mx_priority, item.prefix))
                    a_s.append('%s;%s' % (item.prefix, item.target))

                conf = ['%s;%s;%s' %(z.domain, len(mx_s), len(a_s))]
                conf.extend(mx_s)
                conf.extend(a_s)
                req.data.append(';'.join(conf))

        res = self.__request(req)
        return res.data


    def dhcpd_start(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.DHCPD_START
        res = self.__request(req)
        return res.data

    def dhcpd_stop(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.DHCPD_STOP
        res = self.__request(req)
        return res.data

    def dhcpd_status(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.DHCPD_STATUS
        res = self.__request(req)
        return res.data

    def dhcpd_save(self, domain, lan_net, dns1, dns2, macs):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.DHCPD_SAVE
        req.data.append(domain)
        req.data.append('.'.join(lan_net.split('.')[0:3]))
        req.data.append(dns1)
        req.data.append(dns2)
        for m in macs:
            if m.bind:
                req.data.append('%s;%s' % (m.ip.split('.')[3], m.mac))
        res = self.__request(req)
        return res.data

    def tc_clear(self, wan_device, lan_device):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.TC_CLEAR
        req.data.append(wan_device)
        req.data.append(lan_device)
        res = self.__request(req)
        return res.data

    def tc_status(self, wan_device, lan_device):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.TC_STATUS
        req.data.append(wan_device)
        req.data.append(lan_device)
        res = self.__request(req)
        return res.data

    def tc_apply(self, wan_device, lan_device, lan_net, macs, def_limit):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.TC_APPLY
        req.data.append(wan_device)
        req.data.append(lan_device)
        req.data.append('.'.join(lan_net.split('.')[0:3]))
        
        for m in macs:
            if m.allow:
                limit = m.limit or def_limit
                req.data.append('%s;%s;%s;%s;%s' %(m.ip.split('.')[3], limit.up_rate, limit.down_rate, limit.up_ceil, limit.down_ceil))

        res = self.__request(req)
        return res.data

    def ff_status(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.FF_STATUS
        res = self.__request(req)
        return res.data

    def ff_clear(self, wan_device, lan_net):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.FF_CLEAR
        req.data.append(wan_device)
        req.data.append('.'.join(lan_net.split('.')[0:3]))
        res = self.__request(req)
        return res.data

    def ff_apply(self, wan_device, allow_ping, lan_device, lan_net, ins, outs, macs, nats):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.FF_APPLY
        req.data.append(wan_device)
        req.data.append(str(allow_ping))
        req.data.append(lan_device)
        req.data.append('.'.join(lan_net.split('.')[0:3]))
        req.data.append('%s;%s;%s;%s' % (len(ins), len(outs), len(macs), len(nats)))

        for i in ins:
            req.data.append('%s;%s;%s;%s' % (i.s_ip, i.protocol, i.d_ip, i.d_port))
        for i in outs:
            items = [i.domain, i.start, i.end, i.weekdays()]
            if i.macs:
                items.extend(['%s'%m.id for m in i.macs if m.allow])
            req.data.append(';'.join(items))
        for m in macs:
            req.data.append('%s;%s' % (m.id, m.mac))
        for i in nats:
            req.data.append('%s;%s;%s;%s;%s' %(i.s_ip, i.s_port, i.protocol, i.d_ip, i.d_port))

        res = self.__request(req)
        return res.data
    
    def arp_scan(self):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.ARP_SCAN

        res = self.__request(req)
        return res.data

    def arp_sync(self, eth):
        req = Request()
        req.id = 0
        req.size = 1
        req.type = Request.ARP_SYNC
        req.data.append(eth)

        res = self.__request(req)
        rv = {}
        for line in res.data:
            mac, ip = line.split()
            rv[mac] = ip
        return rv

    def __read(self, sock, timeout=2):
        sock.settimeout(timeout)
        resp = Response()
        resp.ParseFromString(sock.recv(1024))
        return resp

    def __write(self, sock, request):
        sock.sendto(request.SerializeToString() + '\n', self.host)

    def __request(self, request):
        sock = socket.socket(type=socket.SOCK_DGRAM)
        requests = Package.split(request)
        for req in requests:
            i=0
            while True:
                try:
                    self.__write(sock, req)
                    i+=1
                    resp = self.__read(sock)
                    if resp.type == Response.SUCCESS:
                        break
                except socket.timeout:
                    if i==3:
                        raise Exception(u'请求错误超过%s次，请求不可达。' % i)
        try:
            rv = {}
            while True:
                resp = self.__read(sock, timeout=self.TIMEOUT)
                print '#############'
                print resp
                if not Package.link(rv, resp):
                    raise Exception(u'错误的包格式')
                if Package.is_full(rv):
                    return Package.assembly(rv)
        except socket.timeout:
            raise Exception(u'响应超时，请检查网络或重试。')

