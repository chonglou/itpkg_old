__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.web

from brahma.views import BaseHandler
from brahma.plugins.wiki.store import WikiDao
from brahma.plugins.wiki.forms import WikiForm
from brahma.env import cache
from brahma.plugins.wiki.cache import get_wiki


class WikiHandler(BaseHandler):
    def get(self, name=None):

        if name:
            wiki = get_wiki(name)
            title = wiki.title if wiki else name
            wikiItems = list()
        else:
            title = "知识库"

            @cache.cache("wiki", expire=3600 * 24)
            def list_wiki():
                return [("/wiki/%s" % name, title) for name, title, created in
                        WikiDao.list_page(datetime.datetime.min, datetime.datetime.max)]

            wikiItems = list_wiki()
            wiki = None

        buttons = []
        if name and self.current_user:
            url = "/wiki/%s" % name
            if wiki:
                if self.__can_edit(wiki):
                    buttons.append((url, "REFRESH", "info", "刷新"))
                    buttons.append((url, "PUT", "primary", "编辑"))
                    buttons.append((url, "DELETE", "danger", "删除"))
            else:
                buttons.append((url, "PUT", "primary", "创建"))
        self.render_page("wiki/index.html", title=title, wiki=wiki, wikiItems=wikiItems, index="/wiki/",
                         buttons=buttons)

    @tornado.web.authenticated
    def put(self, name=None):
        if name:
            wiki = WikiDao.get(name)
            form = WikiForm("wiki", "编辑知识库[%s]" % name, "/wiki/%s" % name, True)
            if wiki:
                if self.__can_edit(wiki):
                    form.title.data = wiki.title
                    form.content.data = wiki.body
                    self.render_form_widget(form=form)
                else:
                    self.render_message_widget(messages=["你没有编辑[%s]页的权限" % name])
            else:
                self.render_form_widget(form=form)

    @tornado.web.authenticated
    def post(self, name=None):
        fm = WikiForm(formdata=self.request.arguments)

        if name:
            messages = []
            if self.check_captcha():
                if fm.validate():
                    wiki = WikiDao.get(name=name)
                    if wiki:
                        if self.__can_edit(wiki):
                            WikiDao.set(name, fm.title.data, fm.content.data)
                            get_wiki(name, True)
                            self.render_message_widget(ok=True)
                            return
                        else:
                            messages.append("你没有编辑[%s]页的权限" % name)
                    else:
                        WikiDao.set(name, fm.title.data, fm.content.data, self.current_user["id"])
                        self.render_message_widget(ok=True)
                        return
                else:
                    messages.extend(fm.messages())
            else:
                messages.append("验证码不对")
            self.render_message_widget(messages=messages)

    @tornado.web.authenticated
    def delete(self, name=None):
        messages = []
        if name:
            wiki = WikiDao.get(name=name)
            if wiki:
                if self.__can_edit(wiki):
                    WikiDao.delete(name)
                    self.render_message_widget(ok=True)
                    return
                else:
                    messages.append("你没有删除[%s]的权限" % name)
            else:
                messages.append("知识库[%s]不存在" % name)
            self.render_message_widget(messages=messages)

    def __can_edit(self, wiki):
        return wiki.author == self.current_user["id"] or self.is_admin()


handlers = [
    (r"/wiki/(.*)", WikiHandler),
]