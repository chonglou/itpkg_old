module Linux
  module Cdn
    BIND9_VERSION='9.10.1'
    GEOIP_VERSION='1.6.3'
    CDN_BUILD='/tmp/build/cdn'
    CDN_PATH='/opt/cdn'


    module_function
    def install

      <<-EOF
#!/bin/sh

mkdir -p #{CDN_BUILD}
cd #{CDN_BUILD}

apt-get -y install geoip

wget ftp://ftp.isc.org/isc/bind9/#{BIND9_VERSION}/bind-#{BIND9_VERSION}.tar.gz
./configure --prefix=/usr --sysconfdir=/etc --sbindir=/usr/bin --localstatedir=/var --disable-static --enable-threads --with-openssl --with-geoip
make
make install

EOF
    end
  end
end