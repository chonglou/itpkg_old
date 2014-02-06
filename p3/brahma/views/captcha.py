__author__ = 'zhengjitang@gmail.com'

import random

import tornado.options
import tornado.web
from PIL import Image, ImageDraw, ImageFont


class Captcha:
    def __init__(self,
                 num,
                 font_path='font.ttc',
                 bg_color=(255, 255, 255),
                 font_color=None,
                 font_size=20):
        self.__gap = 5
        self.__chars = ''.join(random.sample("一切有为法如梦幻泡影如露亦如电应作如是观", num))
        self.__fontSize = font_size
        self.__fontColor = font_color
        self.__size = ((font_size + self.__gap) * num, font_size + self.__gap * 3)
        self.__font = ImageFont.truetype(tornado.options.options.app_store + "/" + font_path, font_size)
        self.__image = Image.new("RGB", self.__size, bg_color)

    def __rotate(self):
        self.__image.rotate(random.randint(0, 30), expand=0)

    def __draw_text(self, pos, txt, fill):
        draw = ImageDraw.Draw(self.__image)
        draw.text(pos, txt, font=self.__font, fill=fill)
        del draw

    def __rand_rgb(self):
        return random.randint(0, 255), random.randint(0, 255), random.randint(0, 255)

    def __rand_point(self):
        (width, height) = self.__size
        return random.randint(0, width), random.randint(0, height)

    def __draw_rand_line(self, num):
        draw = ImageDraw.Draw(self.__image)
        for i in range(0, num):
            draw.line([self.__rand_point(), self.__rand_point()], self.__rand_rgb())
        del draw

    def draw(self):
        start = 0
        for i in range(0, len(self.__chars)):
            char = self.__chars[i]
            x = start + self.__fontSize * i + random.randint(0, self.__gap) + self.__gap * i
            self.__draw_text(
                (x, random.randint(0, self.__gap * 2)),
                char,
                self.__fontColor if self.__fontColor else self.__rand_rgb())
            self.__rotate()
        self.__draw_rand_line(12)

    def save(self, path):
        self.__image.save(path)

    def get_chars(self):
        return self.__chars

    def file(self):
        import tempfile

        tf = tempfile.NamedTemporaryFile("w+b", suffix=".png")
        self.__image.save(tf.name)
        return tf


class CaptchaHandler(tornado.web.RequestHandler):
    def get(self):
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