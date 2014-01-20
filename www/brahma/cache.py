__author__ = 'zhengjitang@gmail.com'

from brahma.env import cache


def get_qr_png(invalidate=False):
    @cache.cache("site.png")
    def qr():
        import qrcode
        from brahma.store.site import SettingDao

        qr = qrcode.QRCode(
            version=1,
            error_correction=qrcode.ERROR_CORRECT_L,
            #box_size=10,
            border=1,
        )

        qr.add_data("<a href='http://%s'>%s</a>" % (SettingDao.get("site.domain"), SettingDao.get("site.title")))
        qr.make(fit=True)

        import io

        buf = io.BytesIO()
        img = qr.make_image()
        img.save(buf, "PNG")
        return buf.getvalue()

    if invalidate:
        cache.invalidate(qr, "site.png")
    return qr()


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