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

smtpd_tls_cert_file=/etc/dovecot/server.crt
smtpd_tls_key_file=/etc/dovecot/server.key
smtpd_use_tls=yes
smtpd_tls_auth_only = yes

smtpd_sasl_type = dovecot
smtpd_sasl_path = private/auth
smtpd_sasl_auth_enable = yes

smtpd_recipient_restrictions =
	permit_sasl_authenticated,
	permit_mynetworks,
	reject_unauth_destination

myhostname = itpkg.localhost.localdomain
alias_maps = hash:/etc/postfix/aliases
alias_database = hash:/etc/postfix/aliases.db
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
query = SELECT destination FROM email_aliases WHERE source='%s'
EOF

cp master.cf master.cf.orig
#sed -i "s/smtp      inet  n       -       n       -       -       smtpd/smtp      inet  n       -       -       -       -       smtpd -v/g"  master.cf
sed -i "s/smtp      inet  n       -       n       -       -       smtpd/smtp      inet  n       -       -       -       -       smtpd/g"  master.cf
sed -i "s/#submission inet n       -       n       -       -       smtpd/submission inet n       -       -       -       -       smtpd/g"  master.cf
sed -i "s/#smtps     inet  n       -       n       -       -       smtpd/smtps     inet  n       -       -       -       -       smtpd/g"  master.cf

postalias /etc/postfix/aliases

# dovecot
cd /etc/dovecot
cat > dovecot.conf << "EOF"
protocols = imap lmtp

mail_location = maildir:/var/mail/vhosts/%d/%n
mail_privileged_group = mail
disable_plaintext_auth = yes
auth_mechanisms = plain login
#log_path = /var/log/dovecot.log
#auth_verbose = yes
#mail_debug = yes
postmaster_address = ops@itpkg.com

ssl_cert = </etc/dovecot/server.crt
ssl_key = </etc/dovecot/server.key
ssl = required

passdb {
  driver = sql
  args = /etc/dovecot/sql.conf.ext
}
userdb {
  driver = static
  args = uid=vmail gid=vmail home=/var/mail/vhosts/%d/%n
}

service lmtp {
  unix_listener /var/spool/postfix/private/dovecot-lmtp {
    mode = 0600
    user = postfix
    group = postfix
  }
}

service imap-login {
  inet_listener imap {
    port = 0
  }
}

service auth {
  unix_listener /var/spool/postfix/private/auth {
    mode = 0660
    user = postfix
    group = postfix
  }

  unix_listener auth-userdb {
    mode = 0600
    user = vmail
  }

  user = dovecot
}

service auth-worker {
  user = vmail
}
EOF

cat > sql.conf.ext << "EOF"
driver = mysql
connect = host=127.0.0.1 dbname=itpkg user=email password=PASSWORD
default_pass_scheme = SHA512-CRYPT
password_query = SELECT email as user, password FROM email_users WHERE email='%u';
EOF

# ssl certs
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out server.key
chmod 400 server.key
openssl req -new -key server.key -out server.csr -subj "/C=US/ST=California/L=Goleta/O=itpkg/CN=itpkg.com"
openssl x509 -req -days 3650 -in server.csr -signkey server.key -out server.crt
chmod 444 server.crt




cat >> /etc/services << "EOF"
smtps 465/tcp
EOF

sed -i -e "s/PASSWORD/$password/g" /etc/postfix/virtual-*s.cf /etc/dovecot/sql.conf.ext

# vmail user
mkdir -p /var/mail/vhosts
groupadd -g 5000 vmail
useradd -g vmail -u 5000 vmail -d /var/mail
passwd -l vmail
chown -R vmail:vmail /var/mail/vhosts
chown -R vmail:dovecot /etc/dovecot
chmod -R o-rwx /etc/dovecot


systemctl restart postfix
systemctl restart dovecot
