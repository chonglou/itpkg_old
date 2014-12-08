#!/bin/sh

password=$(pwgen 16)
mysql -u root -h localhost -e "
GRANT SELECT ON itpkg.email_users TO 'email'@'localhost' IDENTIFIED BY '$password';
GRANT SELECT ON itpkg.email_domains TO 'email'@'localhost' IDENTIFIED BY '$password';
GRANT SELECT ON itpkg.email_aliases TO 'email'@'localhost' IDENTIFIED BY '$password';
FLUSH PRIVILEGES;"



systemctl restart postfix
systemctl restart dovecot
