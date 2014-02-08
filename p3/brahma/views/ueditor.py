__author__ = 'zhengjitang@gmail.com'

import logging

import tornado.web

from brahma.env import attach_dir


class FileHandler(tornado.web.RequestHandler):
    def check_xsrf_cookie(self):
        pass

    def _list_files(self, types={".gif", ".png", ".jpg", ".jpeg", ".bmp"}):
        import os

        val = list()

        path = "%s/%s" % (attach_dir, self.__user_path())
        l = len(path)
        for root, dirs, files in os.walk(path, True):
            for name in files:
                ext = os.path.splitext(name)[1]
                if ext in types:
                    val.append("%s%s/%s" % (self.__user_path(), root[l:], name))

        #logging.debug("%s" % val)
        return val

    def _upload_remote(self, url, path=None, types={".gif", ".png", ".jpg", ".jpeg", ".bmp"}):

        import tornado.httpclient, os

        client = tornado.httpclient.HTTPClient()
        ext = os.path.splitext(url)[1].lower()
        name = None
        if ext in types:
            try:
                response = client.fetch(url)
                name = self.__write(ext, response.body, path)
            except tornado.httpclient.HTTPError:
                logging.exception("抓取图片出错")
        client.close()
        return name

    def _upload_base64(self, field="content", path=None, ext=".png"):
        data = self.get_argument(field)
        try:
            import base64

            data = base64.standard_b64decode(data)
            name = self.__write(ext, data, path)
            return True, name
        except:
            logging.exception("保存涂鸦出错")
            return False, None

    def _upload(self, field='upfile', path=None,
                types={".rar", ".doc", ".docx", ".zip", ".pdf", ".txt", ".swf", ".wmv", ".gif", ".png", ".jpg",
                       ".jpeg", ".bmp"}):
        import os, logging

        rs = []
        for fi in self.request.files[field]:
            fn = fi['filename']
            logging.debug("上传文件%s" % fn)
            ext = os.path.splitext(fn)[1].lower()
            try:
                if ext in types:
                    name = self.__write(ext, fi['body'], path)
                    rs.append((True, name, ext, fn))
                else:
                    rs.append((False, "后缀名%s不支持" % ext, ext, fn))
            except:
                logging.exception("上传文件出错")
                rs.append((False, "服务器出错", ext, fn))
        return rs

    def __write(self, ext, body, path):
        import uuid

        name = "%s%s" % (uuid.uuid4().hex, ext)

        f = "%s/%s" % (self.__physics_path(path), name )
        with(open(f, "wb")) as out:
            out.write(body)

        return "%s/%s" % (self.__virtual_path(path), name)

    def __physics_path(self, path):
        import os

        d = "%s/%s" % (attach_dir, self.__virtual_path(path))
        if not os.path.exists(d):
            os.makedirs(d)
        return d

    def __virtual_path(self, path):
        import datetime

        d = "%s/%s" % (self.__user_path(), datetime.date.today().isoformat())
        return "%s/%s" % (d, path) if path else d

    def __user_path(self):

        jid = self.get_argument("jsessionid")
        uid = None
        if jid and jid != "null":
            from brahma.cache import j_u_id

            uid = j_u_id(jid=jid)
        if uid is None:
            uid = 0

        return "u%d" % uid


class ImageUpHandler(FileHandler):
    def get(self):
        if self.get_argument("fetch"):
            import tornado.options

            self.set_header("Content-Type", "text/javascript")
            rs = list()
            rs.append("'root'")
            rs.extend(map(lambda p: "'%s'" % p, tornado.options.options.app_plugins))
            self.write(
                "updateSavePath([%s]);" % ','.join(rs))

    def post(self):
        import tornado.options

        p = self.get_argument("dir")
        if p not in tornado.options.options.app_plugins:
            p = None

        rs = self._upload(path=p, types={".gif", ".png", ".jpg", ".jpeg", ".bmp"})
        ok, msg, ext, original = rs[0]
        self.write({
            "state": "SUCCESS" if ok else msg,
            "url": msg if ok else "",
            "title": self.get_argument("pictitle"),
            "original": original,
        })


class GetMovieHandler(FileHandler):
    def post(self):
        import urllib.request
        import urllib.parse

        params = urllib.parse.urlencode({
            "method": "item.search",
            "appKey": "myKey",
            "format": "json",
            "kw": self.get_argument("searchKey"),
            "pageNo": 1,
            "pageSize": 20,
            "channelId": self.get_argument("videoType"),
            "inDays": 7,
            "media": "v",
            "sort": "s"
        })

        print("http://api.tudou.com/v3/gw?%s" % params)
        with urllib.request.urlopen("http://api.tudou.com/v3/gw?%s" % params) as f:
            self.write(f.read())


class GetRemoteImageHandler(FileHandler):
    def post(self):
        src = self.get_argument("upfile")
        urls = [self._upload_remote(url) for url in src.split("ue_separate_ue")]
        self.write({"url": "ue_separate_ue".join(urls), 'tip': 'SUCCESS', 'srcUrl': src})


class ImageManagerHandler(FileHandler):
    def post(self):
        self.write("ue_separate_ue".join(self._list_files()))


class ScrawlUpHandler(FileHandler):
    def post(self):
        param = self.get_argument("param", None)
        if param == "tmpImg":
            rs = self._upload(types={".gif", ".png", ".jpg", ".jpeg", ".bmp"})
            ok, msg, ext, original = rs[0]
            self.write("<script>parent.ue_callback('%s', '%s')</script>") % (
                msg if ok else "",
                "SUCCESS" if ok else msg)
        else:
            import base64

            ok, url = self._upload_base64()
            self.write({"state": "SUCCESS" if ok  else "FAIL", "url": url if ok else ""})


class FileUpHandler(FileHandler):
    def post(self):
        rs = self._upload()
        ok, msg, ext, original = rs[0]
        self.write({
            "state": "SUCCESS" if ok else msg,
            "url": msg if ok else "",
            "fileType": ext,
            "original": original,
        })


handlers = [
    (r"/editor/fileUp", FileUpHandler),
    (r"/editor/getMovie", GetMovieHandler),
    (r"/editor/getRemoteImage", GetRemoteImageHandler),
    (r"/editor/imageManager", ImageManagerHandler),
    (r"/editor/imageUp", ImageUpHandler),
    (r"/editor/scrawlUp", ScrawlUpHandler),
]