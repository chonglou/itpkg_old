#!/bin/sh

cd /etc/nginx/itpkg 
openssl genrsa -out key.pem 2048 
openssl req -new -key key.pem  -subj "/C=US/ST=California/L=Goleta/O=itpkg/CN=itpkg.com" -out cert.csr -text 
openssl x509 -req -in cert.csr -sha512 -days 3650  -signkey key.pem -out cert.pem -text
chmod 400 key.pem && chmod 444 cert.pem cert.csr 
