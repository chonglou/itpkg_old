#!/bin/sh

password=$(pwgen 16)
htpasswd -b -c /etc/nginx/itpkg/.htpasswd deploy $password
chmod 600 /etc/nginx/itpkg/.htpasswd

sed -i "s/^ITPKG_DOCKER_PASSWORD=.*/ITPKG_DOCKER_PASSWORD=$password/g" $ITPKG_HOME/shared/.rbenv-vars
