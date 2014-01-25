__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.cache import get_site_info
from brahma.web import Message


class PageNotFoundHandler(tornado.web.RequestHandler):
    def get(self):
        #raise tornado.web.HTTPError(404)
        self.render("message.html", title="出错了", msg=Message(messages=["资源不存在"], goto="/main"), )


class BaseHandler(tornado.web.RequestHandler):
    def log(self, message, flag="INFO"):
        from brahma.store.site import LogDao

        LogDao.add_log(message, flag=flag, user=self.current_user['id'] if self.current_user else None)

    def is_admin(self):
        return self.current_user and self.current_user["admin"]

    def check_non_login(self):
        if self.get_current_user():
            self.render_message_widget(messages=["你已经登录"])
            return False
        return True

    def render_list_widget(self, label, items=list()):
        from brahma.widgets import List

        l = List(self)
        self.write(l.render(label=label, items=items))

    def render_ctlbar_widget(self, act, items):
        from brahma.widgets import CtlBar

        cb = CtlBar(self)
        self.write(cb.render(act, items))
        self.write('<script type="text/javascript">')
        self.write(cb.embedded_javascript())
        self.write('</script>')


    def render_form_widget(self, form):
        from brahma.widgets import Form

        m = Form(self)
        self.write(m.render(form=form))
        self.write('<script type="text/javascript">')
        self.write(m.embedded_javascript())
        self.write('</script>')


    def render_message_widget(self, ok=None, confirm=None, messages=list(), goto=None):
        from brahma.widgets import Message as MessageWidget
        from brahma.web import Message

        m = MessageWidget(self)
        self.write(m.render(msg=Message(ok=ok,confirm=confirm, messages=messages, goto=goto)))
        self.write('<script type="text/javascript">')
        self.write(m.embedded_javascript())
        self.write('</script>')

    def prepare(self):
        if not get_site_info("version"):
            self.redirect("/install")

    #def _handle_request_exception(self, e):
    #    self.render_message(title="出错了", ok=False, messages=[e], goto="/main")

    def check_captcha(self):
        return self.get_argument("captcha") == self.get_secure_cookie("captcha").decode("utf-8")

    def write_error(self, status_code, **kwargs):
        status_code = status_code
        if status_code == 404:
            msg = "资源不存在"
        elif status_code == 500:
            msg = "服务器错误"
        else:
            msg = "未知错误[%d]" % status_code

        self.render_message("出错了！", messages=[msg], goto="/main")
        #super(tornado.web.RequestHandler, self).write_error(status_code, **kwargs)

    def render_message(self, title, ok=None, confirm=None, messages=list(), goto=None):
        self.render("message.html", title=title, msg=Message(ok=ok, confirm=confirm, messages=messages, goto=goto))

    def render_page(self, template_name, title, index=None, **kwargs):
        import uuid

        self.render(template_name,
                    title=title,
                    jsessionid=self.current_user['jid'] if self.current_user else uuid.uuid4().hex,
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


