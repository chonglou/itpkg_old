__author__ = 'zhengjitang@gmail.com'

NAME = "IT-PKG"


def calendar(year, month, dao):
    return list(), list()


def search(keyword):
    return list()


def sitemap():
    return list()


def rss():
    return list()


def navbar(uid=None):
    if uid:
        from brahma.web import NavBar
        nb = NavBar("路由器列表", True)
        nb.add("/itpkg/router", "设备管理")
        from brahma.plugins.itpkg.store import RouterDao
        map(lambda r:nb.add("/itpkg/router/%s"%r.id, r.name), RouterDao.all(uid))
        nb.add('/itpkg/router/1','111')
        nb.add('/itpkg/router/3','333')
        nb.add('/itpkg/router/2','222')
        return nb
    return None


def tags():
    return list()