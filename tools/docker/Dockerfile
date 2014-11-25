FROM scratch
MAINTAINER Jitang Zheng <jitang.zheng@gmail.com>

ADD tmp/rootfs /
USER root

RUN mkdir -p /home
RUN adduser -s /bin/zsh -D deploy
RUN passwd -l deploy
#RUN echo 'deploy ALL=(ALL) NOPASSWD: ALL' > /etc/sudoers.d/deploy 
USER deploy

ENV HOME /home/deploy
ENV ITPKG_HOME /var/www/itpkg
ENV RUBY_VERSION 2.1.5
ENV ITPKG_HOME /var/www/itpkg

#RUN sudo mkdir -p $ITPKG_HOME
#RUN sudo chown deploy:deploy $ITPKG_HOME
RUN mkdir -p /tmp/build
RUN gcc -v

# mysql
#ENV MYSQL_VERSION 5.6
#ENV MYSQL_EXT_VERSION 21-70.1
#RUN cd /tmp/build && wget http://www.percona.com/downloads/Percona-Server-$MYSQL_VERSION/LATEST/source/tarball/percona-server-$MYSQL_VERSION.$MYSQL_EXT_VERSION.tar.gz && tar xvf persona-server-$MYSQL_VERSION.$MYSQL_EXT_VERSION.tar.gz
#RUN cd /tmp/build/persona-server-$MYSQL_VERSION.$MYSQL_EXT_VERSION

USER root
VOLUME ["/etc/nginx/itpkg", "$ITPKG_HOME/shared", "/home/git/repositories"]
EXPOSE 22 443 25 587 465 143 993 53/udp 1194/udp

CMD ["/sbin/init"]
