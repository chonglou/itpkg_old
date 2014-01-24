__author__ = 'zhengjitang@gmail.com'

from wtforms import validators, TextField, HiddenField

from brahma.web import Form, HtmlField


class RouterForm(Form):
    rid = HiddenField()
    name = TextField("名称", validators=[validators.Required()])
    details = HtmlField("详情")