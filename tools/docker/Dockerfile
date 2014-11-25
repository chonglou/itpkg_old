FROM scratch
MAINTAINER Jitang Zheng <jitang.zheng@gmail.com>

ADD rootfs.tar /
USER root

RUN adduser -s /bin/zsh -S deploy
RUN cat /etc/passwd
RUN echo 'deploy ALL=(ALL) NOPASSWD: ALL' > /etc/sudoers.d/deploy 

ENV HOME /home/deploy
ENV ITPKG_HOME /var/www/itpkg
ENV RUBY_VERSION 2.1.5


ENV ITPKG_HOME /var/www/itpkg
RUN mkdir -p $ITPKG_HOME
RUN chown deploy:deploy $ITPKG_HOME
USER deploy


USER root
VOLUME ["/etc/nginx/itpkg", "$ITPKG_HOME/shared", "/home/git/repositories"]
EXPOSE 22 443 25 587 465 143 993 53/udp 1194/udp

CMD ["/sbin/init"]
