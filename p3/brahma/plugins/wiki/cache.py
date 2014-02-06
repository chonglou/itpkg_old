__author__ = 'zhengjitang@gmail.com'

from brahma.env import cache


def get_wiki(name, invalidate=False):
    @cache.cache("wiki/%s" % name)
    def wiki():
        from brahma.plugins.wiki.store import Wiki
        w = Wiki.get(name)
        if w:
            import markdown
            w.body = markdown.markdown(w.body)
        return w.__dict__

    if invalidate:
        cache.invalidate(wiki, "wiki/%s" % name)

    return wiki()