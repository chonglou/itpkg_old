__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.views import BaseHandler


class SelfHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.write({"goto": "/itpkg/"})


class MainHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        self.render_page("itpkg/main.html", "IT-PACKAGE", index="/itpkg/", label="操作说明",
                         items=[
                             "点击左侧控制面板进行操作",
                             "提交数据只是保存配置，下次重启才会生效",
                             "如果需要立刻生效，需要点击应用更改"
                         ]
        )


handlers = [
    (r"/itpkg/", MainHandler),
    (r"/personal/itpkg", SelfHandler),
]