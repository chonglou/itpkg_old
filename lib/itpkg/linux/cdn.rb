require 'securerandom'

module Linux
  module Cdn

    BIND9_VERSION='9.10.1'
    NGINX_VERSION='1.7.7'
    CDN_BUILD='/tmp/build/cdn'
    CDN_PATH='/opt/cdn'


    module_function

    def drop!(host)
      ["DROP USER 'dns'@'#{host}'", 'FLUSH PRIVILEGES'].each { |sql| ActiveRecord::Base.connection.execute(sql) }

    end

    def grant!(host)
      db=Rails.configuration.database_configuration[Rails.env]['database']

      password = SecureRandom.hex 8

      [
          "GRANT SELECT ON `#{db}`.`dns_records` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
          "GRANT SELECT ON `#{db}`.`dns_xfrs` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
          "GRANT UPDATE ON `#{db}`.`dns_counts` TO 'dns'@'#{host}' IDENTIFIED BY '#{password}'",
          'FLUSH PRIVILEGES'
      ].each { |sql| ActiveRecord::Base.connection.execute(sql) }


      password
    end

    def http(memcached, domain, backs, ssl=false)
      #HTTP header Memcached-Expire
      https = <<-EOF
  ssl  on;
  ssl_certificate  ssl/#{domain}.pem;
  ssl_certificate_key  ssl/#{domain}.pem;
  ssl_session_timeout  5m;
  ssl_protocols  SSLv2 SSLv3 TLSv1;
  ssl_ciphers  RC4:HIGH:!aNULL:!MD5;
  ssl_prefer_server_ciphers  on;

      EOF

      <<-EOF
upstream #{domain}.conf {
#{backs.map { |b| "  server #{b};" }.join "\n"}
}

server {
  listen #{ssl ? 443 : 80};
  server_name #{domain};
  access_log /var/log/nginx/#{domain}.access;
  error_log /var/log/nginx/#{domain}.error;

  location / {
    memcached_pass #{memcached};
    default_type text/html;
	  error_page 404 = @fallback;

	  if ($http_pragma ~* "no-cache") {
	     return 404;
	  }
	  if ($http_cache_control ~* "no-cache") {
	     return 404;
	  }
    #{https if ssl}
	  set $enhanced_memcached_key "$request_uri";
	  set $enhanced_memcached_key_namespace "$host";
    set $enhanced_memcached_expire $http_memcached_expire;

	  enhanced_memcached_hash_keys_with_md5 on;
	  enhanced_memcached_pass memcached_upstream;
	}

	location @fallback {
    #proxy_set_header  X-Forwarded-Proto http;
    #proxy_set_header  X-Forwarded-For $proxy_add_x_forwarded_for;
    #proxy_set_header  Host $http_host;
    #proxy_set_header  X-Real-IP $remote_addr;

    #proxy_redirect off;
    proxy_pass http://#{domain}.conf;
	}

}
      EOF
    end

    def nginx_install
      #nginx -V
      <<-EOF
#!/bin/sh

apt-get -y install build-essential libssl-dev libpcre3-dev

#{build_path}

git clone git://github.com/bpaquet/ngx_http_enhanced_memcached_module.git
wget http://nginx.org/download/nginx-#{NGINX_VERSION}.tar.gz
tar xf nginx-#{NGINX_VERSION}.tar.gz
cd nginx-#{NGINX_VERSION}
./configure --prefix=/etc/nginx --conf-path=/etc/nginx/nginx.conf --sbin-path=/usr/bin/nginx --pid-path=/run/nginx.pid --lock-path=/run/lock/nginx.lock --user=http --group=http --http-log-path=/var/log/nginx/access.log --error-log-path=stderr --http-client-body-temp-path=/var/lib/nginx/client-body --http-proxy-temp-path=/var/lib/nginx/proxy --http-fastcgi-temp-path=/var/lib/nginx/fastcgi --http-scgi-temp-path=/var/lib/nginx/scgi --http-uwsgi-temp-path=/var/lib/nginx/uwsgi --with-imap --with-imap_ssl_module --with-ipv6 --with-pcre-jit --with-file-aio --with-http_dav_module --with-http_gunzip_module --with-http_gzip_static_module --with-http_realip_module --with-http_spdy_module --with-http_ssl_module --with-http_stub_status_module --with-http_addition_module --with-http_degradation_module --with-http_flv_module --with-http_mp4_module --with-http_secure_link_module --with-http_sub_module --add-module=#{CDN_BUILD}/ngx_http_enhanced_memcached_module
make
make install

      EOF
    end

    def build_path
      <<-EOF
rm -r #{CDN_BUILD}
mkdir -p #{CDN_BUILD}
cd #{CDN_BUILD}
      EOF
    end

    def bind9_acl(code, country, region=nil)
      id = "#{country}_#{region||'_'}"
      <<-EOF
acl "acl_#{id}" {
  geoip country #{country};
  #{"geoip region #{region};" if region}
};

view "view_#{id}" {
  match-clients { acl_#{id}; };
  dlz "dlz_#{id}" {
    database "mysql
    {host=#{cfg.fetch(:mysql).fetch :host} dbname=#{Rails.configuration.database_configuration[Rails.env]['database']} user=dns pass=#{cfg.fetch(:mysql).fetch :password} ssl=false}
    {SELECT zone FROM dns_records WHERE zone = '$zone$' AND code='#{code}'}
    {SELECT ttl, type, mx_priority, IF(type = 'TXT', CONCAT('\"',data,'\"'), data) AS data FROM dns_records WHERE zone = '$zone$' AND host = '$record$' AND type <> 'SOA' AND type <> 'NS' AND code='#{code}'}
    {SELECT ttl, type, data, primary_ns, resp_person, serial, refresh, retry, expire, minimum FROM dns_records WHERE zone = '$zone$' AND (type = 'SOA' OR type='NS') AND code='#{code}'}
    {SELECT ttl, type, host, mx_priority, IF(type = 'TXT', CONCAT('\"',data,'\"'), data) AS data, resp_person, serial, refresh, retry, expire, minimum FROM dns_records WHERE zone = '$zone$' AND type <> 'SOA' AND type <> 'NS' AND code='#{code}'}
    {SELECT zone FROM dns_xfrs where zone='$zone$' AND client = '$client$' AND code='#{code}' limit 1}";
    {UPDATE dns_counts SET count=count+1, update_at='NOW()' WHERE zone ='%zone%' AND AND code='#{code}'}";
  };
};

      EOF
    end

    # INSERT INTO `dns_records`
    # (`id`,`zone`,`host`,`type`,`data`,`ttl`,`mx_priority`,`refresh`,`retry`,`expire`,`minimum`,`serial`,`resp_person`,`primary_ns`)
    # VALUES
    # (1, 'example.com', '@', 'SOA', NULL, 180, NULL, 10800, 7200, 604800, 86400, 2011091101, 'admins.mail.hotmail.com', '77.84.21.84'),
    # (2, 'example.com', '@', 'NS', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    # (3, 'example.com', '@', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    # (4, 'example.com', 'www', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    # (5, 'xn--unicode-example.com', '@', 'SOA', NULL, 180, NULL, 10800, 7200, 604800, 86400, 2011091101, 'admins.mail.hotmail.com', '77.84.21.84'),
    # (6, 'xn--unicode-example.com', '@', 'NS', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    # (7, 'xn--unicode-example.com', '@', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL),
    # (8, 'xn--unicode-example.com', 'www', 'A', '77.84.21.84', 180, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)
    # ;
    # dig soa example.com @localhost
    def bind9_install(cfg)
      #named -V
      lines = []
      lines << <<-BASH
#!/bin/sh

apt-get -y install libgeoip-dev geoip-bin geoip-database libmysqlclient-dev build-essential libssl-dev

#{build_path}

wget ftp://ftp.isc.org/isc/bind9/#{BIND9_VERSION}/bind-#{BIND9_VERSION}.tar.gz
tar xf bind-#{BIND9_VERSION}.tar.gz
cd bind-#{BIND9_VERSION}
./configure --prefix=/usr --sysconfdir=/etc --sbindir=/usr/bin --localstatedir=/var --disable-static --enable-threads --with-openssl --with-geoip --with-dlz-mysql --with-libxml2 -with-libtool
make
make install

groupadd named
useradd -g named -c "BIND" -s /usr/sbin/nologin -d /var/named named

rndc-confgen > /etc/rndc.conf
sed -n '15,23p' /etc/rndc.conf | awk '{$1="";print $0}' > /etc/named.conf

cat >> /etc/named << "EOF"
options {
  directory "/var/named";
  pid-file "/var/run/named.pid";
  geoip-directory "/usr/share/GeoIP";

  listen-on-v6 { none; };
  listen-on { 127.0.0.1; #{cfg.fetch(:bind9).fetch(:host)}; };

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
  dlz "mysql_zone_#{code}" {
    database "mysql
    {host=#{cfg.fetch(:mysql).fetch :host} dbname=#{Rails.configuration.database_configuration[Rails.env]['database']} user=dns pass=#{cfg.fetch(:mysql).fetch :password} ssl=false}
    {SELECT zone FROM dns_records WHERE zone = '$zone$' AND code='*'}
    {SELECT ttl, type, mx_priority, IF(type = 'TXT', CONCAT('\"',data,'\"'), data) AS data FROM dns_records WHERE zone = '$zone$' AND host = '$record$' AND type <> 'SOA' AND type <> 'NS' AND code='*'}
    {SELECT ttl, type, data, primary_ns, resp_person, serial, refresh, retry, expire, minimum FROM dns_records WHERE zone = '$zone$' AND (type = 'SOA' OR type='NS') AND code='*'}
    {SELECT ttl, type, host, mx_priority, IF(type = 'TXT', CONCAT('\"',data,'\"'), data) AS data, resp_person, serial, refresh, retry, expire, minimum FROM dns_records WHERE zone = '$zone$' AND type <> 'SOA' AND type <> 'NS' AND code='*'}
    {SELECT zone FROM dns_xfrs where zone='$zone$' AND client = '$client$'  AND code='*' LIMIT 1}";
    {UPDATE dns_counts SET count=count+1, update_at='NOW()' WHERE zone ='%zone%' AND code='*'}";
  };
};

EOF

      BASH

      bind9_files.each do |k, v|
        lines << "cat > #{k} << \"EOF\""
        lines << v
        lines << 'EOF'
      end

      lines.join "\n"
    end

    def bind9_files
      files = {}
      files['127.0.0.zone'] = <<-EOF
$ORIGIN 0.0.127.in-addr.arpa.

@			1D IN SOA	localhost. root.localhost. (
					42		; serial (yyyymmdd##)
					3H		; refresh
					15M		; retry
					1W		; expiry
					1D )		; minimum ttl

			1D IN NS	localhost.
1			1D IN PTR	localhost.
      EOF
      files['empty.zone'] = <<-EOF
@			1D IN SOA	localhost. root.localhost. (
					42		; serial (yyyymmdd##)
					3H		; refresh
					15M		; retry
					1W		; expiry
					1D )		; minimum ttl

			1D IN NS	localhost.
      EOF
      files['localhost.zone'] = <<-EOF
@			1D IN SOA	@ root (
					42		; serial (yyyymmdd##)
					3H		; refresh
					15M		; retry
					1W		; expiry
					1D )		; minimum ttl

			1D IN NS	@
			1D IN A		127.0.0.1
      EOF
      files['root.hint'] = <<-EOF
;       This file holds the information on root name servers needed to
;       initialize cache of Internet domain name servers
;       (e.g. reference this file in the "cache  .  <file>"
;       configuration file of BIND domain name servers).
;
;       This file is made available by InterNIC
;       under anonymous FTP as
;           file                /domain/named.cache
;           on server           FTP.INTERNIC.NET
;       -OR-                    RS.INTERNIC.NET
;
;       last update:    June 2, 2014
;       related version of root zone:   2014060201
;
; formerly NS.INTERNIC.NET
;
.                        3600000  IN  NS    A.ROOT-SERVERS.NET.
A.ROOT-SERVERS.NET.      3600000      A     198.41.0.4
A.ROOT-SERVERS.NET.      3600000      AAAA  2001:503:BA3E::2:30
;
; FORMERLY NS1.ISI.EDU
;
.                        3600000      NS    B.ROOT-SERVERS.NET.
B.ROOT-SERVERS.NET.      3600000      A     192.228.79.201
B.ROOT-SERVERS.NET.      3600000      AAAA  2001:500:84::B
;
; FORMERLY C.PSI.NET
;
.                        3600000      NS    C.ROOT-SERVERS.NET.
C.ROOT-SERVERS.NET.      3600000      A     192.33.4.12
C.ROOT-SERVERS.NET.      3600000      AAAA  2001:500:2::C
;
; FORMERLY TERP.UMD.EDU
;
.                        3600000      NS    D.ROOT-SERVERS.NET.
D.ROOT-SERVERS.NET.      3600000      A     199.7.91.13
D.ROOT-SERVERS.NET.      3600000      AAAA  2001:500:2D::D
;
; FORMERLY NS.NASA.GOV
;
.                        3600000      NS    E.ROOT-SERVERS.NET.
E.ROOT-SERVERS.NET.      3600000      A     192.203.230.10
;
; FORMERLY NS.ISC.ORG
;
.                        3600000      NS    F.ROOT-SERVERS.NET.
F.ROOT-SERVERS.NET.      3600000      A     192.5.5.241
F.ROOT-SERVERS.NET.      3600000      AAAA  2001:500:2F::F
;
; FORMERLY NS.NIC.DDN.MIL
;
.                        3600000      NS    G.ROOT-SERVERS.NET.
G.ROOT-SERVERS.NET.      3600000      A     192.112.36.4
;
; FORMERLY AOS.ARL.ARMY.MIL
;
.                        3600000      NS    H.ROOT-SERVERS.NET.
H.ROOT-SERVERS.NET.      3600000      A     128.63.2.53
H.ROOT-SERVERS.NET.      3600000      AAAA  2001:500:1::803F:235
;
; FORMERLY NIC.NORDU.NET
;
.                        3600000      NS    I.ROOT-SERVERS.NET.
I.ROOT-SERVERS.NET.      3600000      A     192.36.148.17
I.ROOT-SERVERS.NET.      3600000      AAAA  2001:7FE::53
;
; OPERATED BY VERISIGN, INC.
;
.                        3600000      NS    J.ROOT-SERVERS.NET.
J.ROOT-SERVERS.NET.      3600000      A     192.58.128.30
J.ROOT-SERVERS.NET.      3600000      AAAA  2001:503:C27::2:30
;
; OPERATED BY RIPE NCC
;
.                        3600000      NS    K.ROOT-SERVERS.NET.
K.ROOT-SERVERS.NET.      3600000      A     193.0.14.129
K.ROOT-SERVERS.NET.      3600000      AAAA  2001:7FD::1
;
; OPERATED BY ICANN
;
.                        3600000      NS    L.ROOT-SERVERS.NET.
L.ROOT-SERVERS.NET.      3600000      A     199.7.83.42
L.ROOT-SERVERS.NET.      3600000      AAAA  2001:500:3::42
;
; OPERATED BY WIDE
;
.                        3600000      NS    M.ROOT-SERVERS.NET.
M.ROOT-SERVERS.NET.      3600000      A     202.12.27.33
M.ROOT-SERVERS.NET.      3600000      AAAA  2001:DC3::35
; End of File
      EOF
    end

  end
end