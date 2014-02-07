__author__ = 'zhengjitang@gmail.com'


class Wiki(object):
    def __init__(self, name, title, body, author, last_edit):
        self.name = name
        self.title = title
        self.body = body
        self.author = author
        self.last_edit = last_edit


tables = [
    ("wikis", True, True, True, [
        "name_ VARCHAR(128) UNIQUE NOT NULL",
        "title_ VARCHAR(255) NOT NULL",
        "body_ TEXT NOT NULL",
        "author_ INTEGER NOT NULL",
        "last_edit_ DATETIME NOT NULL",
    ]),
]