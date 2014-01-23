__author__ = 'zhengjitang@gmail.com'

from brahma.env import cache


def j_u_id(jid, uid=None, invalidate=False):
    c = cache.get_cache("jsessionid", type="dbm")
    if invalidate:
        c.remove_value(jid)
        return
    if uid:
        c.set_value(jid, uid)
        return uid
    return c.get_value(jid)




def get_advert(key, invalidate=False):
    @cache.cache("site/advert/%s" % key)
    def info():
        from brahma.store.site import SettingDao

        return SettingDao.get("site.advert.%s" % key)

    if invalidate:
        cache.invalidate(info, "site/advert/%s" % key)

    return info()


def get_site_info(key, invalidate=False, encrypt=False):
    @cache.cache("site/%s" % key)
    def info():
        from brahma.store.site import SettingDao

        return SettingDao.get("site.%s" % key, encrypt)

    if invalidate:
        cache.invalidate(info, "site/%s" % key)

    return info()