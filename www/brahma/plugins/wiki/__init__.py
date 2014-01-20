__author__ = 'zhengjitang@gmail.com'

NAME = "知识库"


def calendar(year, month, day):
    from brahma.plugins.wiki.store import WikiDao
    from brahma.env import cache
    import datetime

    if day:
        @cache.cache("wiki/%04d/%02d/%02d" % ( year, month, day), expire=2600 * 24)
        def list_wiki():
            dt = datetime.datetime(year, month, day)
            return [("/wiki/%s" % w.name, w.title) for w in WikiDao.list_wiki(dt, dt + datetime.timedelta(days=1))]
    else:
        @cache.cache("wiki/%04d/%02d" % ( year, month), expire=2600 * 24)
        def list_wiki():
            return [("/wiki/%s" % w.name, w.title) for w in
                    WikiDao.list_wiki(datetime.datetime(year, month, 1), datetime.datetime(year, month + 1, 1))]

    return list_wiki()


def search(keyword):
    return list()


def sitemap():
    return list()


def rss():
    return list()


def tags():
    return [("/wiki/", "知识库")]