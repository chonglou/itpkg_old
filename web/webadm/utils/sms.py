#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

import httplib
import urllib
from StringIO import StringIO
from lxml import etree


class MobileService:
    def __init__(self, host, port, url, name, password, corp_id):
        self.host = host
        self.port = port
        self.url = url
        self.name = name
        self.password = password
        self.corp_id = corp_id

    def send_mms(self):
        pass

    def send_sms(self, product_id, msg, phones):
        conn = httplib.HTTPConnection(self.host, self.port)
        params = urllib.urlencode({
            'sname': self.name,
            'spwd': self.password,
            'scorpid': self.corp_id,
            'sprdid': product_id,
            'sdst': ','.join(phones),
            'smsg': msg.encode('utf-8')
        })
        headers = {
            "Content-Type": "application/x-www-form-urlencoded",
            'Connection': 'Keep-Alive',
            'Content-Length': len(params),
            'cache-control': 'no-cache'
        }
        conn.request('POST', self.url, params, headers)
        response = conn.getresponse()
        if response.status == httplib.OK:
            data = response.read()
            root = etree.fromstring(data)
            ret_val = {}
            for child in root:
                ret_val[child.tag.rsplit('}', 1)[-1]] = child.text
            return ret_val
        else:
            raise Exception(response.status, response.reason)




if __name__ == '__main__':
    sms = MobileService('dx.lmobile.cn', 6003, '/submitdata//Service.asmx/g_Submit', 'dlwangj1', 'baifen100', '')
    response = sms.send_sms('121', u'吃饭啦！', ['15002700652', '13911510811', '13488771047', '13476020202'])
    print response['MsgState']