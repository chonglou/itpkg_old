#!/bin/sh

password=$(pwgen 16)

mysql -u root -h localhost -e "GRANT SELECT ON itpkg.dns_records TO dns@localhost IDENTIFIED BY '$password';GRANT SELECT ON itpkg.dns_xfrs TO dns@localhost IDENTIFIED BY '$password';GRANT UPDATE ON itpkg.dns_counts TO dns@localhost IDENTIFIED BY '$password';FLUSH PRIVILEGES"

rndc-confgen > /etc/rndc.conf
sed -n '15,23p' /etc/rndc.conf | awk '{$1="";print $0}' > /etc/named.conf

cat >> /etc/named << "EOF"
options {
  directory "/var/named";
  pid-file "/var/run/named.pid";
  geoip-directory "/usr/share/GeoIP";

  listen-on-v6 { none; };
  listen-on { any; };

  allow-recursion { 127.0.0.1; };
  allow-transfer { none; };
  allow-update { none; };

  version none;
  hostname none;
  server-id none;

};

zone "localhost" IN {
    type master;
    file "localhost.zone";
};

zone "0.0.127.in-addr.arpa" IN {
    type master;
    file "127.0.0.zone";
};

zone "255.in-addr.arpa" IN {
    type master;
    file "empty.zone";
};

zone "0.in-addr.arpa" IN {
    type master;
    file "empty.zone";
};

zone "." IN {
    type hint;
    file "root.hint";
};

view "default" {
  dlz "mysql_zone" {
    database "mysql
    {host=localhost dbname=itpkg user=dns pass=PASSWORD ssl=false}
    {SELECT zone FROM dns_records WHERE zone = '$zone$' AND code=0}
    {SELECT ttl, type, mx_priority, IF(type = 'TXT', CONCAT('\"',data,'\"'), data) AS data FROM dns_records WHERE zone = '$zone$' AND host = '$record$' AND type <> 'SOA' AND type <> 'NS' AND code=0}
    {SELECT ttl, type, data, primary_ns, resp_person, serial, refresh, retry, expire, minimum FROM dns_records WHERE zone = '$zone$' AND (type = 'SOA' OR type='NS') AND code=0}
    {SELECT ttl, type, host, mx_priority, IF(type = 'TXT', CONCAT('\"',data,'\"'), data) AS data, resp_person, serial, refresh, retry, expire, minimum FROM dns_records WHERE zone = '$zone$' AND type <> 'SOA' AND type <> 'NS' AND code=0}
    {SELECT zone FROM dns_xfrs where zone='$zone$' AND client = '$client$'  AND code=0 LIMIT 1}";
    {UPDATE dns_counts SET count=count+1, update_at='NOW()' WHERE zone ='%zone%' AND code=0}";
  };
};
EOF

sed -i -e "s/PASSWORD/$password/g" /etc/named.conf
systemctl restart named
