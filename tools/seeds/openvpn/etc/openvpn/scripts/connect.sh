#!/bin/sh
. /etc/openvpn/scripts/config.sh
mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -e "INSERT INTO vpn_logs(host_id, flag,email,message,created) values('<%= fetch(:host_id) %>', 'C','$common_name', '$trusted_ip;$trusted_port;$ifconfig_pool_remote_ip;$remote_port_1;$bytes_received;$bytes_sent', 'NOW()')"
