#!/bin/sh

password=$(pwgen 16)
mysql -u root -h localhost -e "
GRANT SELECT ON itpkg.email_users TO 'email'@'localhost' IDENTIFIED BY '$password';
GRANT SELECT ON itpkg.email_domains TO 'email'@'localhost' IDENTIFIED BY '$password';
GRANT SELECT ON itpkg.email_aliases TO 'email'@'localhost' IDENTIFIED BY '$password';
FLUSH PRIVILEGES;"

cd /etc/postfix
mv main.cf main.cf.orig
cat > main.cf  << "EOF"
smtpd_banner = $myhostname ESMTP $mail_name (IT-PACKAGE)
biff = no
append_dot_mydomain = no
#delay_warning_time = 4h
readme_directory = no

smtpd_tls_cert_file=/etc/dovecot/dovecot.pem
smtpd_tls_key_file=/etc/dovecot/private/dovecot.pem
smtpd_use_tls=yes
smtpd_tls_auth_only = yes

smtpd_sasl_type = dovecot
smtpd_sasl_path = private/auth
smtpd_sasl_auth_enable = yes

smtpd_recipient_restrictions =
	permit_sasl_authenticated,
	permit_mynetworks,
	reject_unauth_destination

myhostname = localhost.localdomain
alias_maps = hash:/etc/aliases
alias_database = hash:/etc/aliases
myorigin = /etc/mailname
mydestination = localhost
relayhost =
mynetworks = 127.0.0.0/8 [::ffff:127.0.0.0]/104 [::1]/128
mailbox_size_limit = 0
recipient_delimiter = +
inet_interfaces = all

virtual_transport = lmtp:unix:private/dovecot-lmtp
virtual_mailbox_domains = mysql:/etc/postfix/virtual-mailbox-domains.cf
virtual_mailbox_maps = mysql:/etc/postfix/virtual-mailbox-maps.cf
virtual_alias_maps = mysql:/etc/postfix/virtual-alias-maps.cf
EOF

cat > virtual-mailbox-domains.cf  << "EOF"
user = email
password = PASSWORD
hosts = 127.0.0.1
dbname = itpkg
query = SELECT 1 FROM email_domains WHERE name='%s'
EOF
cat > virtual-mailbox-maps.cf  << "EOF"
user = email
password = PASSWORD
hosts = 127.0.0.1
dbname = itpkg
query = SELECT 1 FROM email_users WHERE email='%s'
EOF
cat > virtual-alias-maps.cf  << "EOF"
user = email
password = PASSWORD
hosts = 127.0.0.1
dbname = itpkg
query = SELECT destination FROM virtual_aliases WHERE source='%s'
EOF

mv master.cf master.cf.orig
cat > master.cf << "EOF"
smtp      inet  n       -       -       -       -       smtpd
submission inet n       -       -       -       -       smtpd
smtps     inet  n       -       -       -       -       smtpd
EOF
cat >> /etc/services << "EOF"
smtps 465/tcp
smtps 465/udp
EOF


sed -i -e "s/PASSWORD/$password/g" /etc/postfix/virtual-*s.cf
systemctl restart postfix
systemctl restart dovecot
