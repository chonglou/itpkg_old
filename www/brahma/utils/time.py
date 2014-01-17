__author__ = 'zhengjitang@gmail.com'

import datetime

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