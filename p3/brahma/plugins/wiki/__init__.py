__author__ = 'zhengjitang@gmail.com'

NAME = "知识库"


def user(uid):
    import datetime
    from brahma.plugins.wiki.store import WikiDao
    from brahma.env import cache

    @cache.cache("wiki/%s" % uid, expire=3600 * 24)
    def list_wiki():
        return [("/wiki/%s" % w.name, w.title) for w in
                WikiDao.list_wiki(datetime.datetime.min, datetime.datetime.max, author=uid)]

    return list(), list_wiki()


def calendar(year, month, day):
    from brahma.plugins.wiki.store import WikiDao
    from brahma.env import cache

    if day:
        @cache.cache("wiki/%04d/%02d/%02d" % ( year, month, day), expire=3600 * 24)
        def list_wiki():
            from brahma.utils.time import day_range

            d1, d2 = day_range(year, month, day)
            return [("/wiki/%s" % w.name, w.title) for w in WikiDao.list_wiki(d1, d2)]
    else:
        @cache.cache("wiki/%04d/%02d" % ( year, month), expire=3600 * 24)
        def list_wiki():
            from brahma.utils.time import month_range

            d1, d2 = month_range(year, month)
            return [("/wiki/%s" % w.name, w.title) for w in WikiDao.list_wiki(d1, d2)]

    return list(), list_wiki()


def search(keyword):
    return list(), list()


def sitemap():
    def list_wiki():
        from brahma.plugins.wiki.store import WikiDao

        return [("wiki/%s" % w.name, w.created, "monthly", 0.5) for w in WikiDao.all()]

    return list_wiki()


def rss():
    return list()


def navbar(uid=None):
    return None


def tags():
    return [("/wiki/", "知识库")]