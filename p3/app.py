__author__ = 'zhengjitang@gmail.com'

if __name__ == "__main__":
    import sys
    import brahma

    if len(sys.argv) == 2:
        if "start" == sys.argv[1]:
            brahma.Server("web.cfg").run()
        elif "debug" == sys.argv[1]:
            brahma.Server("web.cfg", debug=True).run()