__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.views import BaseHandler
from brahma.store.site import SettingDao
from brahma.env import cache_call


class MainHandler(BaseHandler):
    def get(self):
        self.render_page("main.html", title="主页")


class SearchHandler(BaseHandler):
    def post(self):
        import tornado.options, importlib

        keyword = self.get_argument("keyword")
        items = list()
        map(lambda rs: items.append(rs),
            map(
                lambda name: importlib.import_module("brahma.plugins." + name).search(keyword),
                tornado.options.options.app_plugins))

        self.render_page("search.html", title="搜索[%s]" % keyword, keyword=keyword, items=[])


class HelpHandler(BaseHandler):
    def get(self):
        @cache_call("site/help")
        def get_help():
            return SettingDao.get("site.help")

        self.render_page("template.html", index="/help", title="帮助文档", content=get_help())


class AboutMeHandler(BaseHandler):
    def get(self):
        @cache_call("site/aboutMe")
        def get_aboutMe():
            return SettingDao.get("site.aboutMe")

        self.render_page("template.html", index="/aboutMe", title="关于我们", content=get_aboutMe())


class CalendarHandler(BaseHandler):
    def get(self, year, month, day=None):
        self.render_page(
            "template.html",
            title="%s年%s月%s日" % (year, month, day) if day else "%s年%s月" % (year, month),
            content=None)


handlers = [
    (r"/", tornado.web.RedirectHandler, dict(url="/main")),
    (r"/main", MainHandler),
    (r"/search", SearchHandler),
    (r"/aboutMe", AboutMeHandler),
    (r"/help", HelpHandler),
    (r"/calendar/([0-9]+)/([0-9]+)", CalendarHandler),
    (r"/calendar/([0-9]+)/([0-9]+)/([0-9]+)", CalendarHandler)
]
