#!/bin/sh

mkdir -p /etc/openvpn/scripts
password=$(pwgen 16)
mysql -u root -h localhost -e "GRANT SELECT ON itpkg.vpn_users TO 'vpn'@'localhost' IDENTIFIED BY '$password';GRANT INSERT ON itpkg.vpn_logs TO 'vpn'@'localhost' IDENTIFIED BY '$password';FLUSH PRIVILEGES;"


mkdir -p /etc/openvpn/scripts
cd /etc/openvpn/scripts
cat > config.sh << "EOF"
#!/bin/sh
HOST='localhost'
PORT='3306'
USER='vpn'
PASS='PASSWORD'
DB='itpkg'
EOF

cat > login.sh << "EOF"
#!/bin/sh
. /etc/openvpn/script/config.sh
user_id=$(mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -sN -e "select name from vpn_users where name = '$username' AND password = PASSWORD('$password') AND enable=1 AND TO_DAYS(now()) >= TO_DAYS(start_date) AND TO_DAYS(now()) <= TO_DAYS(end_date) ")
[ "$user_id" != '' ] && [ "$user_id" = "$username" ] && logger "user : $username" && logger 'authentication ok.' && exit 0 || logger 'authentication failed.'; exit 1
EOF


cat > connect.sh << "EOF"
#!/bin/sh
. /etc/openvpn/scripts/config.sh
mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -e "INSERT INTO vpn_logs (user, trusted_ip, trusted_port, remote_ip, remote_port, start_time, end_time, received, sent) VALUES('$common_name', '$trusted_ip', '$trusted_port', '$ifconfig_pool_remote_ip', '$remote_port_1', now(), '0000-00-00 00:00:00', '$bytes_received', '$bytes_sent')"
mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -e "UPDATE vpn_users SET online=1 WHERE name='$common_name'"
EOF

cat > disconnect.sh << "EOF"
#!/bin/sh
. /etc/openvpn/scripts/config.sh
mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -e "UPDATE vpn_users SET online=0 WHERE name='$common_name'"
mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -e "UPDATE vpn_logs SET end_time=now(), received='$bytes_received', sent='$bytes_sent' WHERE trusted_ip='$trusted_ip' AND trusted_port='$trusted_port' AND name='$common_name' AND end_time='0000-00-00 00:00:00'"
EOF

chmod +x *.sh


cd ..
cat > server.conf << "EOF"
port 1194
proto udp
dev tun

ca /etc/openvpn/ca.crt
cert /etc/openvpn/server.crt
key /etc/openvpn/server.key
dh /etc/openvpn/dh2048.pem

ifconfig-pool-persist ipp.txt

server 192.168.86.0 255.255.255.0

# push "route 192.168.10.0 255.255.255.0"
push "dhcp-option DNS 192.168.86.1"
push "dhcp-option DNS 8.8.8.8"

comp-lzo
user nobody
client-to-client
username-as-common-name

auth-user-pass-verify /etc/openvpn/script/login.sh via-env

script-security 3 system
client-connect /etc/openvpn/scripts/connect.sh
client-disconnect /etc/openvpn/scripts/disconnect.sh

keepalive 10 120
persist-key
persist-tun
status status.log
verb 3
EOF


cp -r /usr/share/easy-rsa/ /etc/openvpn/easy-rsa
cd /etc/openvpn/easy-rsa
cat >> vars << "EOF"

export KEY_COUNTRY="US"
export KEY_PROVINCE="CA"
export KEY_CITY="Goleta"
export KEY_ORG="itpkg"
export KEY_EMAIL="deploy@itpkg.com"
export KEY_OU="ops"
EOF
. ./vars
./clean-all
./build-dh
./pkitool --initca
./pkitool --server server
cd keys
openvpn --genkey --secret ta.key
cp ca.crt ta.key dh2048.pem server.crt server.key /etc/openvpn

cd ..
./pkitool client

mkdir -p /etc/openvpn/clients
chown deploy:deploy /etc/openvpn/clients

sed -i -e "s/PASSWORD/$password/g" /etc/openvpn/scripts/config.sh

systemctl restart openvpn@server
