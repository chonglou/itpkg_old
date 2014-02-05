__author__ = 'zhengjitang@gmail.com'

from wtforms import validators, TextField, TextAreaField

from brahma.web import Form


class WikiForm(Form):
    title = TextField("标题", validators=[validators.Required()])
    body = TextAreaField("内容", validators=[validators.Required()])