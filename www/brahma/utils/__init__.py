__author__ = 'zhengjitang@gmail.com'


def list_mod(file):
    import os

    val = list()
    file = path(file)
    for root, dirs, files in os.walk(path(file), True):
        for name in files:
            if not name.startswith("__") and name.endswith(".py"):
                #print(file, root, name)
                val.append((root + "." + name).replace(file, '')[1:-3])
    return val


def path(uri):
    import os
    #print(os.path.abspath(os.path.join(os.path.dirname(__file__), uri)))
    return os.path.abspath(os.path.join(os.path.dirname(__file__), uri))

