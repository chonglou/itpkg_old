__author__ = 'zhengjitang@gmail.com'

import datetime


def day_range(year, month, day):
    dt = datetime.datetime(year, month, day)
    return dt, dt + datetime.timedelta(days=1)


def month_range(year, month):
    import logging

    logging.debug("###### %s" % month)
    dt = datetime.datetime(year, month, 1)
    if month == 12:
        year += 1
        month = 1
    else:
        month += 1
    return dt, datetime.datetime(year, month, 1)


def last_months(minimum, count):
    months = list()
    today = datetime.datetime.today()
    last = datetime.date(today.year, today.month, 1)
    months.append(last)
    for i in range(0, count):
        last = last - datetime.timedelta(days=2)
        last = datetime.date(last.year, last.month, 1)
        if last < datetime.date(minimum.year, minimum.month, 1):
            break
        months.append(last)
    return months