__author__ = 'zhengjitang@gmail.com'

NAME = "IT-PKG"


def user(uid):
    return list(), list()


def calendar(year, month, day):
    return list(), list()


def search(keyword):
    return list(), list()


def sitemap():
    return list()


def rss():
    return list()


def navbar(uid=None):
    if uid:
        from brahma.web import NavBar

        nb = NavBar("IT-PACKAGE", True)
        nb.add("/itpkg/group", "用户组")
        nb.add("/itpkg/user", "用户")
        nb.add("/itpkg/limit", "限速规则")
        nb.add("/itpkg/router", "路由设备")
        from brahma.plugins.itpkg.store import RouterDao

        items = [("/itpkg/router/%s" % r.id, "路由-%s" % r.name) for r in RouterDao.all(uid)]
        nb.items.extend(items)
        return nb
    return None


def tags():
    return list()