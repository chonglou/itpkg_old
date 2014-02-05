__author__ = 'zhengjitang@gmail.com'

import tornado.web


class CaptchaHandler(tornado.web.RequestHandler):
    def get(self):
        from brahma.utils.captcha import Captcha

        c = Captcha(4)
        c.draw()
        file = c.file()
        self.set_secure_cookie("captcha", c.get_chars())
        # Set to expire far in the past.
        self.set_header("Expires", 0)
        # Set standard HTTP/1.1 no-cache headers.
        self.set_header("Cache-Control", "no-store, no-cache, must-revalidate")
        # Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        self.set_header("Cache-Control", "post-check=0, pre-check=0")
        # Set standard HTTP/1.0 no-cache header.
        self.set_header("Pragma", "no-cache")
        # return a png
        self.set_header("Content-Type", "image/png")
        self.write(file.read())
        file.close()


handlers = [
    (r"/captcha", CaptchaHandler),
]