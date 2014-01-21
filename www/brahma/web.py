__author__ = 'zhengjitang@gmail.com'

import wtforms_tornado

from wtforms import BooleanField, TextAreaField


class HtmlField(TextAreaField):
    pass


class AgreementField(BooleanField):
    def __init__(self, text="", *args, **kwargs):
        self.text = text
        super().__init__(self, *args, **kwargs)


class Translations(object):
    def gettext(self, string):
        return _MESSAGES.get(string) or string

    def ngettext(self, singular, plural, n):
        print("######## ", singular, plural, n)
        return n


class Form(wtforms_tornado.Form):
    def __init__(self, fid=None, label="表单", action="#", captcha=False, formdata=None):
        self.fid = fid
        self.label = label
        self.action = action
        self.captcha = captcha
        super().__init__(formdata=formdata)

    def messages(self):
        val = []
        for i in self.errors:
            val.append(str(getattr(getattr(getattr(self, i), 'label'), 'text')) +
                       "：" +
                       "".join(self.errors[i]))
        return val

    def from_dict(self, items):
        for k in items.keys():
            getattr(self, k).data = items[k]

    def to_dict(self, items):
        val = {}
        for i in items:
            val[i] = getattr(self, i).data
        return val

    def _get_translations(self):
        return Translations()


_MESSAGES = {
    "This field is required.": "不能为空。",
    "Invalid email address.": "邮箱格式不正确。",
    "Not a valid integer value": "不是整数",
}


class Message:
    def __init__(self, ok=False, confirm=False, messages=list(), goto=None):
        self.ok = ok
        self.confirm = confirm
        self.messages = messages
        self.goto = goto


class NavBar:
    def __init__(self, label):
        self.label = label
        self.items = list()

    def add(self, url, label):
        self.items.append((url, label))