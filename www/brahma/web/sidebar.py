__author__ = 'zhengjitang@gmail.com'


class Sidebar:
    def __init__(self, label):
        self.label = label
        self.items = list()

    def add(self, url, label):
        self.items.append((url, label))