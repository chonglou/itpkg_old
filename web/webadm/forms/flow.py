#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from wtforms import Form, SelectField, RadioField, TextField, HiddenField, IntegerField, validators


class LimitForm(Form):
    host_id = HiddenField()
    item_id = HiddenField()
    caption = TextField(u'名称', [validators.Required(message=u'名称不能为空')])
    up_rate_speed = IntegerField(u'保证上行带宽', [validators.Required(message=u'保证上行带宽不能为空'),
                                             validators.NumberRange(min=10, max=20480,
                                                                    message=u'保证上行带宽应该为整数，在10K-20M之间')])
    down_rate_speed = IntegerField(u'保证下行带宽', [validators.Required(message=u'保证下行带宽不能为空'),
                                               validators.NumberRange(min=10, max=20480,
                                                                      message=u'保证下行带宽应该为整数，在10K-20M之间')])
    up_ceil_speed = IntegerField(u'最大上行带宽', [validators.Required(message=u'最大上行带宽不能为空'),
                                             validators.NumberRange(min=10, max=20480,
                                                                    message=u'最大上行带宽应该为整数，在10K-20M之间')])
    down_ceil_speed = IntegerField(u'最大下行带宽', [validators.Required(message=u'最大下行带宽不能为空'),
                                               validators.NumberRange(min=10, max=20480,
                                                                      message=u'最大下行带宽应该为整数，在10K-20M之间')])


class DefLimitForm(Form):
    host_id = HiddenField()
    limit_id = SelectField(u'限速规则')
    #default = RadioField(u'默认', choices=[(1, u'是'), (0, u'否')], default=0, coerce=int)


class MacLimitForm(Form):
    host_id = HiddenField()
    limit_id = SelectField(u'限速规则')
    mac_id = SelectField(u'MAC地址')
  