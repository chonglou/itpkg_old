__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.env import cache,cache_call
from brahma.store.site import SettingDao


@cache_call("site/info")
def get_site_info():
    def get_site(key):
        return SettingDao.get("site." + key)

    from brahma.web.sidebar import Sidebar
    from brahma.utils.time import last_months

    init = get_site("init")

    sbCal = Sidebar("归档列表")
    sbCal.items.extend(map(lambda dt:(dt.strftime("/calendar/%Y/%m"), dt.strftime("%Y年%m月")), last_months(init, 5)))

    #def add(dt):
    #    sbCal.add("/calendar/" + dt.isoformat(), dt.strftime("%Y年%m月"))


    return {
        "domain": get_site("domain"),
        "title": get_site("title"),
        "keywords": get_site("keywords"),
        "description": get_site("description"),
        "sidebars": [sbCal], #FIXME
        "tags": [], #FIXME
        "topLinks": [
            ("/help", "帮助文档"),
            ("/aboutMe", "关于我们"),
        ], #FIXME
        "adLeft": get_site("ad.left"),
        "adMain": get_site("ad.main"),
    }


class PageNotFoundHandler(tornado.web.RequestHandler):
    def get(self):
        #raise tornado.web.HTTPError(404)
        self.render("message.html",
                    title="提示信息",
                    site=get_site_info(),
                    ok=False,
                    confirm=False,
                    messages=["资源不存在"],
                    is_login=self.get_secure_cookie("user") is not None,
                    index=None,
                    goto="/main")


class BaseHandler(tornado.web.RequestHandler):
    def check_non_login(self):
        if self.get_current_user():
            self.render_message_widget(ok=False, messages=["你已经登录"])
            return False
        return True

    def prepare(self):
        if not cache.get("site/version"):
            v = SettingDao.get("site.version")
            if v:
                cache.set("site/version", v)
            else:
                self.redirect("/install")

    #def _handle_request_exception(self, e):
    #    self.render_message(ok=False, messages=[e], goto="/main")

    def check_captcha(self):
        return self.get_argument("captcha") == self.get_secure_cookie("captcha").decode("utf-8")

    def write_error(self, status_code, **kwargs):
        if status_code == 404:
            msg = "资源不存在"
        elif status_code == 500:
            msg = "服务器错误"
        else:
            msg = "未知错误[" + status_code + "]"

        self.render_message("出错了！", messages=[msg], goto="/main")
        #super(tornado.web.RequestHandler, self).write_error(status_code, **kwargs)

    def render_message(self, title, ok=None, confirm=None, messages=list(), goto=None):
        self.render_page("message.html", title=title, ok=ok, confirm=confirm, messages=messages, goto=goto)

    def render_message_widget(self, ok=None, confirm=None, messages=list(), goto=None):
        self.render("widgets/message.html", ok=ok, confirm=confirm, messages=messages, goto=goto)

    def render_page(self, template_name, index=None, **kwargs):
        self.render(template_name, site=get_site_info(),
                    index=index,
                    is_login=self.get_secure_cookie("user") is not None,
                    **kwargs)

    def get_current_user(self):
        import pickle
        user = self.get_secure_cookie("user")
        return pickle.loads(user) if user else None

    def set_current_user(self, user):
        import pickle
        self.set_secure_cookie("user", pickle.dumps(user))


    def goto_main_page(self):
        self.redirect("/main")


