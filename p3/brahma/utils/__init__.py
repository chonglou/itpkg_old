__author__ = 'zhengjitang@gmail.com'


def walk_plugin(fun):
    import tornado.options

    for p in tornado.options.options.app_plugins:
        fun(p)


def list_mod(file):
    import os

    def path(f):
        return os.path.realpath(f)

    val = list()
    file = path(file)
    for root, dirs, files in os.walk(path(file), True):
        for name in files:
            if not name.startswith("__") and name.endswith(".py"):
                #print(file, root, name)
                val.append((root + "." + name).replace(file, '')[1:-3])
    return val


#def path(uri):
#    import os
#    #print(os.path.abspath(os.path.join(os.path.dirname(__file__), uri)))
#    return os.path.abspath(os.path.join(os.path.dirname(__file__), uri))

