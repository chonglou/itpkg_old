__author__ = 'zhengjitang@gmail.com'

tables = [
    ("wikis", False, True, True, [
        "name_ VARCHAR(128) UNIQUE NOT NULL",
        "title_ VARCHAR(255) NOT NULL",
        "body_ TEXT NOT NULL",
        "author_ INTEGER NOT NULL",
        "last_edit_ DATETIME NOT NULL",
    ]),
]