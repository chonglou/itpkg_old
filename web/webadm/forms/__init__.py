#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import ValidationError

eth_choices = [('eth0', 'eth0'), ('wan', 'wan'), ('lan', 'lan'), ('dmz', 'dmz'), ('eth1', 'eth1'), ('eth2', 'eth2'),
    ('eth3', 'eth3'), ('bak', 'bak')]

def time_of_day_choices():
    rv = []
    for i in range(0, 24):
        rv.append(('%02d:00' % i, '%02d:00' % i))
        rv.append(('%02d:30' % i, '%02d:30' % i))
    return rv


def ip_of_lan_choices(net):
    i1, i2, i3, i4 = net.split('.')
    rv = []
    for i in range(2, 255):
        ip = '%s.%s.%s.%s' % (i1, i2, i3, i)
        rv.append((ip, ip))
    return rv


def nick_name_check():
    message = u'昵称长度应在2-25之间,且只能由字母和数字组成'

    def _check(form, field):
        s = field.data.strip()
        s = s.encode('utf-8')
        l = len(s)
        if l < 2 or l > 25 or not s.isalnum():
            raise ValidationError(message)

    return  _check

  