__author__ = 'zhengjitang@gmail.com'


def listen(name, host, port):
    import logging
    from brahma.utils.redis import Redis
    redis = Redis(name, host=host, port=port)
    while True:
        val = redis.brpop("tasks")
        logging.debug("收到任务[%s]"%val)
