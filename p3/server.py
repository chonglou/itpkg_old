__author__ = 'zhengjitang@gmail.com'


if __name__ == "__main__":
    from brahma.server import Server
    s = Server(9999, "/tmp/brahma", None, None, {}, True)
    s.start()