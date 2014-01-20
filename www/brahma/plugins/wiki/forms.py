__author__ = 'zhengjitang@gmail.com'

from brahma.web import Form
from wtforms import validators,TextField,TextAreaField,HiddenField


class WikiForm(Form):
    title = TextField("标题", validators=[validators.Required()])
    body = TextAreaField("内容", validators=[validators.Required()])