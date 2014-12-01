FROM base/archlinux:latest
MAINTAINER Jitang Zheng <jitang.zheng@gmail.com>

RUN pacman -Syu --noconfirm
RUN pacman -S --needed --noconfirm base-devel geoip geoip-database libmariadbclient wget git hardening-wrapper

# bind9
RUN abs extra/nginx \
&& cp -r /var/abs/extra/nginx /tmp \
&& cd /tmp/nginx \
&& wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/arch/mnt/tmp/nginx-memcache.patch \
&& patch -p0 < nginx-memcache.patch \
&& makepkg --asroot \
&& pacman -U --noconfirm nginx-*.pkg.tar.xz 

RUN rm -r /tmp/nginx
RUN pacman -Scc --noconfirm
RUN systemctl enable nginx

VOLUME ["/etc/nginx/itpkg.conf", "/etc/nginx/ssl"]
EXPOSE 80 443
USER root

CMD ["/sbin/init"]

