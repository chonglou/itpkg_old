__author__ = 'zhengjitang@gmail.com'

import tornado.web


class ImageUpHandler(tornado.web.RequestHandler):
    def get(self):
        from brahma.jobs import TaskSender

        TaskSender.qr()
        TaskSender.rss()
        TaskSender.sitemap()
        TaskSender.robots()
        pass


class FileUpHandler(tornado.web.RequestHandler):
    def get(self):
        pass


class GetMovieHandler(tornado.web.RequestHandler):
    def get(self):
        pass


class GetRemoteImageHandler(tornado.web.RequestHandler):
    def get(self):
        pass


class ImageManagerHandler(tornado.web.RequestHandler):
    def get(self):
        pass


class ScrawlUpHandler(tornado.web.RequestHandler):
    def get(self):
        pass


#fixme 未实现
handlers = [
    (r"/editor/fileUp", FileUpHandler),
    (r"/editor/getMovie", GetMovieHandler),
    (r"/editor/getRemoteImage", GetRemoteImageHandler),
    (r"/editor/imageManager", ImageManagerHandler),
    (r"/editor/imageUp", ImageUpHandler),
    (r"/editor/scrawlUp", ScrawlUpHandler),
]