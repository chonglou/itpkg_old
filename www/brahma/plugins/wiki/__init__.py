__author__ = 'zhengjitang@gmail.com'

NAME = "知识库"


def calendar(year, month, day):
    from brahma.plugins.wiki.store import WikiDao
    from brahma.env import cache_call
    import datetime
        
    @cache_call("wiki/%4d/%2d/%2d"%( year, month, day))
    def list_wiki_1():
        dt = datetime.datetime(year, month,day)
        return map(lambda w:("/wiki/%s" % w.name, w.title), WikiDao.list_wiki(dt, dt+datetime.timedelta(days=1)))

    @cache_call("wiki/%4d/%2d"%( year, month))
    def list_wiki_2():
        dt = datetime.datetime(year, month, 1)
        return map(lambda w:("/wiki/%s" % w.name, w.title), WikiDao.list_wiki(dt, datetime.datetime(year,month+1, 1)-datetime.timedelta(days=1)))
    return list_wiki_1() if day else list_wiki_2()


def search(keyword):
    return list()


def sitemap():
    return list()


def rss():
    return list()


def tags():
    return [("/wiki/","知识库")]