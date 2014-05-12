#!/bin/sh
if [ $# != 1 ] ; then
	echo "USAGE: $0 domain"
	exit 1;
fi
mkdir -pv root
openssl genrsa -out root/root-key.pem 2048
openssl req -new -key root/root-key.pem -out root/root-req.csr -text -subj "/C=US/ST=California/L=Goleta/O=brahma/OU=ops/CN=$1/emailAddress=jitang.zheng@gmail.com"
openssl x509 -req -in root/root-req.csr -out root/root-cert.pem -sha512 -signkey root/root-key.pem -days 3650 -text -extensions v3_ca
