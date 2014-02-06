__author__ = 'zhengjitang@gmail.com'

import tornado.web

from brahma.cache import cache, get_site_info
from brahma.views import BaseHandler, plugin_cards_links
from brahma.store import User


class MainHandler(BaseHandler):
    def get(self):
        @cache.cache("site/index")
        def get_index():
            import tornado.options

            if tornado.options.options.app_plugins:
                index = "/%s/" % tornado.options.options.app_plugins[0]
            else:
                index = "/aboutMe"
            return index

        self.redirect(get_index(), permanent=True)


class SearchHandler(BaseHandler):
    def post(self):
        import importlib

        keyword = self.get_argument("keyword")
        cards, links = plugin_cards_links(lambda p: importlib.import_module("brahma.plugins." + p).search(keyword))

        self.render_template(title="搜索[%s]" % keyword, keyword=keyword, cards=cards, links=links)


class HelpHandler(BaseHandler):
    def get(self):
        from brahma.cache import get_site_info

        self.render_template(index="/help", title="帮助文档", content=get_site_info("help"))


class AboutMeHandler(BaseHandler):
    def get(self):
        from brahma.cache import get_site_info

        self.render_template(index="/aboutMe", title="关于我们", content=get_site_info("aboutMe"))


class CalendarHandler(BaseHandler):
    def get(self, year, month, day=None):
        year = int(year)
        month = int(month)
        day = int(day) if day else None

        import importlib

        cards, links = plugin_cards_links(
            lambda p: importlib.import_module("brahma.plugins." + p).calendar(year, month, day))
        self.render_template(
            title="%04d年%02d月%02d日" % (year, month, day) if day else "%04d年%02d月" % (year, month),
            cards=cards, links=links)


class UserHandler(BaseHandler):
    def get(self, uid=None):
        manager = get_site_info("manager", encrypt=True)
        if uid:
            user = User.get(uid)
            if user:
                #fixme 变成bin了
                import json

                if user.contact:
                    contact = json.loads(user.contact)
                    content = contact['details']
                else:
                    content = None

                import importlib

                cards, links = plugin_cards_links(lambda p: importlib.import_module("brahma.plugins." + p).user(uid))

                self.render_template(title=user.username, index="/user/", content=content, cards=cards, links=links)
            else:
                self.write_error(404)

        else:
            def user2card(u):
                return "/user/%s" % u.id, u.logo, u.username, "" if "localhost" in u.email or manager == u.id else u.email

            cards = [user2card(u) for u in User.all()]
            self.render_template("用户列表", "/user/", cards=cards)


handlers = [
    (r"/", tornado.web.RedirectHandler, dict(url="/main")),
    (r"/main", MainHandler),
    (r"/search", SearchHandler),
    (r"/aboutMe", AboutMeHandler),
    (r"/help", HelpHandler),
    (r"/user/([0-9]*)", UserHandler),
    (r"/calendar/([0-9]+)/([0-9]+)", CalendarHandler),
    (r"/calendar/([0-9]+)/([0-9]+)/([0-9]+)", CalendarHandler)
]
