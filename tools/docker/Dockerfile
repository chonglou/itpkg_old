FROM ubuntu:latest

ENV DEBIAN_FRONTEND noninteractive
ENV ITPKG_HOME /var/www/itpkg
ENV RUBY_VERSION 2.1.4

RUN apt-get update
RUN apt-get -y install bash
RUN apt-get -y install git build-essential libssl-dev bash wget
RUN apt-get -y install libgit2-dev cmake pkg-config libmysqlclient-dev
RUN apt-get -y install nginx nodejs
RUN apt-get clean

RUN useradd -s /bin/bash -m deploy
RUN passwd -l deploy
RUN mkdir -p $ITPKG_HOME
RUN chown deploy:deploy $ITPKG_HOME
USER deploy

#ADD init.sh /tmp/init.sh
#RUN /tmp/init.sh
#RUN rm /tmp/init.sh


EXPOSE 443
EXPOSE 25
EXPOSE 465
EXPOSE 1194/udp
EXPOSE 993

CMD ['/bin/bash']


