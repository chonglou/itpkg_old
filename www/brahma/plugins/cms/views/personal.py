__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.views import BaseHandler


class ManagerHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, act):
        if act == "tag":
            self.write("tags")


class CmsHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        items = [
            ("article", "文章管理"),
            ("comment", "评论管理"),
        ]
        if self.is_admin():
            items.append(("tag", "标签管理"))
        return self.render_ctlbar_widget(act="/personal/cms",
                                         items=items)


handlers = [
    (r"/personal/cms", CmsHandler),
    (r"/personal/cms/(.*)", ManagerHandler),
]