__author__ = 'zhengjitang@gmail.com'

NAME = "知识库"


def user(uid):
    import datetime
    from brahma.plugins.wiki.store import WikiDao
    from brahma.env import cache

    @cache.cache("wiki/%s" % uid, expire=3600 * 24)
    def list_wiki():
        return [("/wiki/%s" % name, title) for name, title in
                WikiDao.list_page(datetime.datetime.min, datetime.datetime.max, author=uid)]

    return list(), list_wiki()


def calendar(year, month, day):
    from brahma.plugins.wiki.store import WikiDao
    from brahma.env import cache

    if day:
        @cache.cache("wiki/%04d/%02d/%02d" % ( year, month, day), expire=3600 * 24)
        def list_wiki():
            from brahma.utils.time import day_range

            d1, d2 = day_range(year, month, day)
            return [("/wiki/%s" % name, title) for name, title, created in WikiDao.list_page(d1, d2)]
    else:
        @cache.cache("wiki/%04d/%02d" % ( year, month), expire=3600 * 24)
        def list_wiki():
            from brahma.utils.time import month_range

            d1, d2 = month_range(year, month)
            return [("/wiki/%s" % name, title) for name, title, created in WikiDao.list_page(d1, d2)]

    return list(), list_wiki()


def search(keyword):
    return list(), list()


def sitemap():
    def list_wiki():
        import datetime
        from brahma.plugins.wiki.store import WikiDao
        return [("wiki/%s" % name, created, "monthly", 0.5) for name, title, created in WikiDao.list_page(datetime.datetime.min, datetime.datetime.max)]

    return list_wiki()


def rss():
    return list()


def navbar(uid=None):
    return None


def tags():
    return [("/wiki/", "知识库")]