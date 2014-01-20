__author__ = 'zhengjitang@gmail.com'

import logging
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker


class Database:
    def __init__(self, uri, echo, plugins=list()):
        import importlib
        from brahma.models import Base

        importlib.import_module("brahma.models")
        for p in plugins:
            importlib.import_module("brahma.plugins." + p + ".models")

        self.__session = sessionmaker(expire_on_commit=False)
        engine = create_engine(uri, echo=echo)
        logging.debug("检查数据库")
        Base.metadata.create_all(engine)
        self.__session.configure(bind=engine)

    def session(self):
        return self.__session()


