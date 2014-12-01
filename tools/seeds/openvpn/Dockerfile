FROM base/archlinux:latest
MAINTAINER Jitang Zheng <jitang.zheng@gmail.com>

RUN pacman -Syu --noconfirm
RUN pacman -S --needed base-devel openvpn
RUN cd /tmp \
&& wget https://aur.archlinux.org/packages/pa/pam_mysql/pam_mysql.tar.gz \
&& tar xf pam_mysql.tar.gz \
&& cd pam_mysql \
&& makepkg --asroot \
&& pacman -U --noconfirm pam_mysql-*.pkg.tar.xz 
RUN rm -r /tmp/pam_mysql
RUN systemctl enable openvpn

EXPOSE 1194/udp
USER root

CMD ["/sbin/init"]

