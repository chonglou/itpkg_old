#!/bin/sh
if [ $# != 2 ] ; then
	echo "USAGE: $0 name domain"
	exit 1;
fi
mkdir $1
openssl genrsa -out $1/server-key.pem 2048
openssl req -new -key $1/server-key.pem -out $1/server-req.csr -text -subj "/C=US/ST=California/L=Goleta/O=brahma/OU=ops/CN=$2/emailAddress=jitang.zheng@gmail.com"
openssl x509 -req -in $1/server-req.csr -CA root/root-cert.pem -CAkey root/root-key.pem -CAcreateserial -days 3650 -out $1/server-cert.pem -text
