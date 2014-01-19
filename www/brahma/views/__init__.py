__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.env import cache
from brahma.store.site import SettingDao
from brahma.web import Message


class PageNotFoundHandler(tornado.web.RequestHandler):
    def get(self):
        #raise tornado.web.HTTPError(404)
        self.render("message.html", msg=Message(messages=["资源不存在"], goto="/main"), )


class BaseHandler(tornado.web.RequestHandler):
    def is_admin(self):
        #FIXME
        return True

    def check_non_login(self):
        if self.get_current_user():
            self.render_message_widget(msg=Message(messages=["你已经登录"]))
            return False
        return True

    def render_form_widget(self, form):
        from brahma.widgets import Form

        m = Form(self)
        self.write(m.render(form=form))
        self.write('<script type="text/javascript">')
        self.write(m.embedded_javascript())
        self.write('</script>')


    def render_message_widget(self, msg):
        from brahma.widgets import Message

        m = Message(self)
        self.write(m.render(msg=msg))
        self.write('<script type="text/javascript">')
        self.write(m.embedded_javascript())
        self.write('</script>')

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
        self.render("message.html", msg=Message(title=title, ok=ok, confirm=confirm, messages=messages, goto=goto))

    def render_page(self, template_name, title, index=None, **kwargs):

        from brahma.env import cache_call

        @cache_call("nav/cal")
        def get_nav_cal():
            from brahma.web import NavBar
            from brahma.utils.time import last_months

            nv_cal = NavBar("归档列表")
            init = SettingDao.get("site.init")
            nv_cal.items.extend(
                map(lambda dt: (dt.strftime("/calendar/%Y/%m"), dt.strftime("%Y年%m月")), last_months(init, 5)))
            return nv_cal

        @cache_call("tagCloud")
        def get_tag_cloud():
            #FIXME
            return [("http://" + SettingDao.get("site.domain"), SettingDao.get("site.title"))]

        if "tagLinks" not in kwargs:
            kwargs['tagLinks'] = get_tag_cloud()

        if "navItems" not in kwargs:
            kwargs['navItems'] = [get_nav_cal()]

        self.render(template_name, title=title,
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


