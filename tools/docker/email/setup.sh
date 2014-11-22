#!/bin/sh
mysqld_safe --user=mysql &
sleep 10

mkdir -p /etc/openvpn/scripts
password=$(pwgen 16)
mysql -u root -h localhost -e "
GRANT SELECT ON itpkg.email_users TO 'email'@'localhost' IDENTIFIED BY '$password';
GRANT SELECT ON itpkg.email_hosts TO 'email'@'localhost' IDENTIFIED BY '$password';
GRANT SELECT ON itpkg.email_domains TO 'email'@'localhost' IDENTIFIED BY '$password';
GRANT SELECT ON itpkg.email_aliases TO 'email'@'localhost' IDENTIFIED BY '$password';
FLUSH PRIVILEGES;"


#sed -i -e "s/PASSWORD/$password/g" /etc/pam.d/openvpn /etc/openvpn/scripts/config.sh

