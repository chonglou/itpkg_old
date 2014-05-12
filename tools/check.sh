#!/bin/sh
if [ $# != 1 ] ; then
	echo "USAGE: $0 name"
	exit 1;
fi
openssl verify -CAfile root/root-cert.pem $1/server-cert.pem
