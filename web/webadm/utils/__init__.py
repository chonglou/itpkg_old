#!/usr/bin/env python
#coding=utf-8



__author__ = 'letminba@gmail.com'

class ArpItem:
    def __init__(self, line):
        rv = line.split()
        if len(rv) == 5:
            self.address = rv[0]
            self.hw_type= rv[1]
            self.hw_address=rv[2]
            self.flags_mask = rv[3]
            self.i_face=rv[4]
        elif len(rv) == 3:
            self.address = rv[0]
            self.hw_address = rv[1]
            self.i_face = rv[2]


  