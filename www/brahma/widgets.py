__author__ = 'zhengjitang@gmail.com'

import tornado.web


class Form(tornado.web.UIModule):
    def render(self, form):
        return self.render_string("widgets/form.html", form=form)


class Sidebar(tornado.web.UIModule):
    def render(self):
        return self.render_string("widgets/sidebar.html")
