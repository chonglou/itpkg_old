#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

import socket

def is_speed_limit(speed):
    try:
        i = int(speed)
        if i > 9 and i < 1000000:
            return True
    except ValueError:
        pass
    return False


def is_time(time):
    ts = time.split(':')
    if len(ts) == 2:
        try:
            h = int(ts[0])
            m = int(ts[1])
            if h >= 0 and h < 24 and m >= 0 and m < 60:
                return True
        except ValueError:
            pass
    return False


def is_ip(ip):
    ips = ip.split('.')
    if len(ips) == 4:
        try:
            for s in ips:
                i = int(s)
                if i < 0 or i > 254:
                    return False
            return True
        except ValueError:
            pass
    return False


def get_ip_by_hostname(hostname):
    return socket.gethostbyname(hostname)


  