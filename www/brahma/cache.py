__author__ = 'zhengjitang@gmail.com'

from brahma.env import cache


def get_advert(key, invalidate=False):
    @cache.cache("site/advert/%s" % key)
    def info():
        from brahma.store.site import SettingDao

        return SettingDao.get("site.advert.%s" % key)

    if invalidate:
        cache.invalidate(info, "site/advert/%s" % key)

    return info()


def get_site_info(key, invalidate=False):
    @cache.cache("site/%s" % key)
    def info():
        from brahma.store.site import SettingDao

        return SettingDao.get("site.%s" % key)

    if invalidate:
        cache.invalidate(info, "site/%s" % key)

    return info()