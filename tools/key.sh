#!/bin/sh
if [ $# != 1 ] ; then
	echo "USAGE: $0 file"
	exit 1;
fi
openssl rsa -noout -text -in $1
