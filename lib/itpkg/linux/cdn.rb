require 'securerandom'
# fixme remove

module Linux

  # todo
  module Cdn

    BIND9_VERSION='9.10.1'
    NGINX_VERSION='1.7.7'
    CDN_BUILD='/tmp/build/cdn'
    CDN_PATH='/opt/cdn'


    module_function


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
  server_name localhost;
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




  end
end