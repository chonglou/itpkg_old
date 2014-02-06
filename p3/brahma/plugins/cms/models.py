__author__ = 'zhengjitang@gmail.com'

tables = [
    ("cms_tags", True, True, False, [
        "name_ VARCHAR(16) UNIQUE NOT NULL",
        "keep_ TINYINT NOT NULL DEFAULT 0",
        "visits_ INTEGER NOT NULL DEFAULT 0",
    ]),
    ("cms_articles", True, True, True, [
        "author_ INTEGER NOT NULL",
        "logo_ VARCHAR(128)",
        "title_ VARCHAR(128) NOT NULL",
        "summary_ VARCHAR(255) NOT NULL",
        "body_ TEXT NOT NULL",
        "last_edit_ DATETIME NOT NULL",
        "visits_ INTEGER NOT NULL DEFAULT 0",
    ]),
    ("cms_article_tag", True, True, False, [
        "article_ INTEGER NOT NULL",
        "tag_ INTEGER NOT NULL",
    ]),
    ("cms_comments", True, True, True, [
        "article_ INTEGER NOT NULL",
        "user_ INTEGER NOT NULL",
        "reply_ INTEGER",
        "content_ TEXT NOT NULL",
    ]),

]