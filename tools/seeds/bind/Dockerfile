FROM base/archlinux:latest
MAINTAINER Jitang Zheng <jitang.zheng@gmail.com>

RUN pacman -Syu --noconfirm
RUN pacman -S --needed --noconfirm base-devel geoip geoip-database libmariadbclient wget

# bind9
RUN abs extra/bind \
&& cp -r /var/abs/extra/bind /tmp \
&& cd /tmp/bind \
&& wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/arch/mnt/tmp/bind-mysql-geoip.patch \
&& patch -p0 < bind-mysql-geoip.patch \
&& makepkg --asroot \
&& pacman -U --noconfirm bind-*.pkg.tar.xz 

RUN rm -r /tmp/bind
RUN pacman -Scc --noconfirm
RUN systemctl enable named

VOLUME ["/etc/named.conf", "/etc/rndc.key"]
EXPOSE 53/udp
USER root

CMD ["/sbin/init"]

