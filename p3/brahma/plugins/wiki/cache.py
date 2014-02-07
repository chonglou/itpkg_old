__author__ = 'zhengjitang@gmail.com'

from brahma.env import cache


def get_wiki(name, invalidate=False):
    @cache.cache("wiki/%s" % name)
    def wiki():
        from brahma.plugins.wiki.store import WikiDao

        w = WikiDao.get(name)
        if w:
            import markdown

            w.body = markdown.markdown(w.body)
        return w

    if invalidate:
        cache.invalidate(wiki, "wiki/%s" % name)

    return wiki()