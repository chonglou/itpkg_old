__author__ = 'zhengjitang@gmail.com'

import datetime

import tornado.web

from brahma.views import BaseHandler
from brahma.plugins.wiki.store import WikiDao
from brahma.plugins.wiki.forms import WikiForm
from brahma.web import Message
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
                return [("/wiki/%s" % w.name, w.title) for w in
                        WikiDao.list_wiki(datetime.datetime.min, datetime.datetime.max)]

            wikiItems = list_wiki()
            wiki = None
        buttons = []
        if name and self.current_user:
            url = "/wiki/%s" % name
            if wiki:
                if self.__can_edit(wiki):
                    buttons.append((url, "PUT", "primary", "编辑"))
                    buttons.append((url, "DELETE", "danger", "删除"))
            else:
                buttons.append((url, "PUT", "primary", "创建"))
        self.render_page("wiki/index.html", title=title, wiki=wiki, wikiItems=wikiItems, index="/wiki/",
                         buttons=buttons)

    @tornado.web.authenticated
    def put(self, name=None):
        if name:
            wiki = WikiDao.get_wiki(name)
            form = WikiForm("wiki", "编辑知识库[%s]" % name, "/wiki/%s" % name, True)
            if wiki:
                if self.__can_edit(wiki):
                    form.title.data = wiki.title
                    form.body.data = wiki.body
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
                    wiki = WikiDao.get_wiki(name=name)
                    if wiki:
                        if self.__can_edit(wiki):
                            WikiDao.set_wiki(name, fm.title.data, fm.body.data)
                            get_wiki(name, True)
                            self.render_message_widget(ok=True)
                            return
                        else:
                            messages.append("你没有编辑[%s]页的权限" % name)
                    else:
                        WikiDao.add_wiki(name, fm.title.data, fm.body.data, self.current_user["id"])
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
            wiki = WikiDao.get_wiki(name=name)
            if wiki:
                if self.__can_edit(wiki):
                    WikiDao.del_wiki(name)
                    self.render_message_widget(ok=True)
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