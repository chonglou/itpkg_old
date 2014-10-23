require 'securerandom'

module Linux
  module OpenVpn
    module_function

    def sql_connection(host)
      db=Rails.configuration.database_configuration[Rails.env]['database']
      <<-EOF
CREATE USER 'vpn'@'#{host}' IDENTIFIED BY '#{SecureRandom.hex 8}';
GRANT SELECT ON `#{db}`.'vpn_users' TO 'vpn'@'#{host}';
GRANT INSERT ON `#{db}`.'vpn_logs' TO 'vpn'@'#{host}';
FLUSH PRIVILEGES;
      EOF
    end

    def config_files
      db=Rails.configuration.database_configuration[Rails.env]['database']
      cf = {}

      #crypt(0) -- Used to decide to use MySQL's PASSWORD() function or crypt()
      #0 = No encryption. Passwords in database in plaintext. NOT recommended!
      #1 = Use crypt
      #2 = Use MySQL PASSWORD() function

      cf['/etc/pam.d/openvpn'] = <<-EOF
auth sufficient pam_mysql.so user=vpn passwd=CHANGE_ME host=CHANGE_ME db=#{db} [table=vpn_users] usercolumn=name passwdcolumn=passwd [where=enable=1 AND start_date<=CURDATE() AND end_date>=CURDATE()] sqllog=0 crypt=2
account required pam_mysql.so user=vpn passwd=CHANGE_ME host=CHANGE_ME db=#{db} [table=vpn_users] usercolumn=name passwdcolumn=passwd [where=enable=1 AND start_date<=CURDATE() AND end_date>=CURDATE()] sqllog=0 crypt=2
      EOF

      cf['/etc/openvpn/scripts/config.sh'] = <<-EOF
#!/bin/sh
HOST='CHANGE_ME'
PORT='3306'
USER='vpn'
PASS='CHANGE_ME'
DB='#{db}'
      EOF

      cf['/etc/openvpn/scripts/connect.sh'] = <<-EOF
#!/bin/sh
. /etc/openvpn/scripts/config.sh
mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -e "INSERT INTO vpn_logs(flag,username,message) values('C','$common_name', '$trusted_ip;$trusted_port;$ifconfig_pool_remote_ip;$remote_port_1;$bytes_received;$bytes_sent')"
      EOF
      cf['/etc/openvpn/scripts/disconnect.sh'] = <<-EOF
#!/bin/sh
. /etc/openvpn/scripts/config.sh
mysql -h$HOST -P$PORT -u$USER -p$PASS $DB -e "INSERT INTO vpn_logs(flag,username,message) values('D','$common_name', '$trusted_ip;$trusted_port;$ifconfig_pool_remote_ip;$remote_port_1;$bytes_received;$bytes_sent')"
      EOF

      cf['/etc/openvpn/openvpn.conf']=<<-EOF
port 1194
proto udp
dev tun

#KEYS
ca /etc/openvpn/keys/ca.crt
cert /etc/openvpn/keys/vpn.example.org.crt
key /etc/openvpn/keys/vpn.example.org.key
dh /etc/openvpn/keys/dh2048.pem

ifconfig-pool-persist ipp.txt
#CHANGEME: ip for the clients
server 10.0.1.0 255.255.255.0
#CHANGEME: routes pushed to the client
push "route 172.16.1.0 255.255.255.0"
push "route 10.0.0.0 255.0.0.0"
push "dhcp-option DNS 8.8.8.8"


comp-lzo
user nobody
client-to-client
username-as-common-name

plugin /usr/lib/openvpn/openvpn-auth-pam.so openvpn

script-security 3 system
client-connect /etc/openvpn/scripts/connect.sh
client-disconnect /etc/openvpn/scripts/disconnect.sh

keepalive 10 120
persist-key
persist-tun
status status.log
verb 3
      EOF

    end
  end
end