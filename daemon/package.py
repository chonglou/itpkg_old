#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from protocols_pb2 import Request, Response

class Package():
    ''

    @staticmethod
    def is_full(rv):
        return rv and rv.values()[0].size == len(rv)

    @staticmethod
    def split(pkg):
        if not pkg.data:
            return [pkg]

        kv = []
        cur_len = 0
        i = 0
        j = 0
        for s in pkg.data:
            cur_len += len(s)
            if cur_len > 500:
                kv.append((j, i + 1))
                j = i + 1
                cur_len = 0

            i += 1

        length = len(pkg.data)
        if j < length:
            kv.append((j, length))

        id = 0
        size = len(kv)
        rv = []
        for i, j in kv:
            if isinstance(pkg, Request):
                item = Request()
            elif isinstance(pkg, Response):
                item = Response()
            else:
                raise Exception(u'未知数据类型')
            item.id = id
            item.size = size
            item.type = pkg.type

            while i < j:
                item.data.append(pkg.data[i])
                #print 'id=%s size=%s i=%s j=%s resp.id=%s resp.data.len=%s %s' %(id, size, i, j, resp.id, len(resp.data), resp.data)
                i += 1

            rv.append(item)
            id += 1
        return rv

    @staticmethod
    def link(rv, pkg):
        """
        如果包格式不同 清空 返回false
        如果包类型不同 清空 添加
        """
        if rv:
            tmp = rv.values()[0]
            if type(tmp) != type(pkg):
                rv.clear()
                return False
            if tmp.type != pkg.type:
                rv.clear()
        rv[pkg.id] = pkg
        return True

    @staticmethod
    def assembly(rv):
        tmp = rv[0]
        item = None
        if tmp.size == len(rv):
            if isinstance(tmp, Request):
                item = Request()
            elif isinstance(tmp, Response):
                item = Response()
            else:
                raise Exception(u'错误的包类型')


        if not item:
            raise Exception(u'包错误')
        item.id = 0
        item.size = tmp.size
        item.type = tmp.type

        for i in range(0, len(rv)):
            for s in rv[i].data:
                item.data.append(s)

        rv.clear()
        return item



