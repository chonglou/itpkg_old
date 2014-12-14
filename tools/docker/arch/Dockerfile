FROM base/archlinux:latest
MAINTAINER Jitang Zheng <jitang.zheng@gmail.com>

# upgrade system
RUN pacman -Syu --noconfirm \
&& pacman -S --noconfirm --needed base-devel wget pwgen expect git geoip abs s-nail net-tools dnsutils vim-minimal lsof curl sudo net-snmp\
 openssh iptables mongodb elasticsearch \
# itpkg
nodejs libmariadbclient imagemagick cmake libgit2 protobuf zeromq \
# services
percona-server percona-server-clients redis memcached nginx \
# openvpn
easy-rsa openvpn \
# mail
postfix dovecot \
# docker registry
python2 python2-pip python2-virtualenv \
# htpasswd 
apr-util \
&& pacman -Scc --noconfirm 


# useradd
RUN useradd -s /bin/bash -m deploy \
&& passwd -l deploy \
&& echo 'deploy ALL=(ALL) NOPASSWD: ALL' > /etc/sudoers.d/deploy

ENV HOME /home/deploy
ENV ITPKG_HOME /var/www/itpkg
ENV PATH $HOME/.rbenv/shims:$HOME/.rbenv/bin:$PATH
ENV RUBY_VERSION 2.1.5
ENV CONFIGURE_OPTS --disable-install-doc
USER deploy

# vim
RUN sudo pacman -R --noconfirm vi \
&& cd /usr/bin \
&& sudo ln -sv vim vi \
&& cp /usr/share/vim/vim74/vimrc_example.vim $HOME/.vimrc \
&& echo "set nobackup" >> $HOME/.vimrc \
&& sudo cp $HOME/.vimrc /root/

# ssh
RUN sudo sed -i "s/#PermitRootLogin/PermitRootLogin/g" /etc/ssh/sshd_config \
&& echo 'root:changeme' | sudo chpasswd 

# rbenv
RUN git clone https://github.com/sstephenson/rbenv.git ~/.rbenv \
&& git clone https://github.com/sstephenson/ruby-build.git ~/.rbenv/plugins/ruby-build \
&& git clone https://github.com/sstephenson/rbenv-vars.git ~/.rbenv/plugins/ruby-vars \
&& echo "ITPKG_HOME=$ITPKG_HOME" >>  ~/.bashrc \
&& echo 'PATH=$HOME/.rbenv/bin:$PATH' >> ~/.bashrc \
&& echo 'export ITPKG_HOME PATH' >> ~/.bashrc \
&& echo 'eval "$(rbenv init -)"' >> ~/.bashrc \
&& echo 'gem: --no-rdoc --no-ri' >> ~/.gemrc \
&& rbenv install $RUBY_VERSION \
&& rbenv global $RUBY_VERSION \
&& gem install bundler 

ADD mnt /tmp/mnt

# itpkg install
RUN sudo mkdir -pv $ITPKG_HOME \
&& sudo chown -R deploy:deploy $ITPKG_HOME \
&& cp -r /tmp/mnt$ITPKG_HOME/shared $ITPKG_HOME/shared \
&& git clone https://github.com/chonglou/itpkg.git --single-branch --branch master --depth 1 $ITPKG_HOME/current \
&& cd $ITPKG_HOME/current \
&& git rev-parse --short HEAD > $ITPKG_HOME/current/REVISION \
&& rm -r .git .ssh \
&& mv .rbenv-vars $ITPKG_HOME/shared/.rbenv-vars \
&& echo 'RAILS_ENV=production' >> $ITPKG_HOME/shared/.rbenv-vars \
&& bundle install --without test development \
&& for i in .rbenv-vars .ssh tmp log; do ln -sv $ITPKG_HOME/shared/$i $i; done 

# gitolite
RUN sudo mkdir -p /home/git \
&& sudo chown -R git:git /home/git \
&& sudo chmod 700 /home/git \
&& sudo usermod -d /home/git git 
USER git
ENV HOME /home/git

RUN mkdir -p ~/local/gitolite \
&& git clone https://github.com/sitaramc/gitolite.git --single-branch --branch master /tmp/gitolite \
&& /tmp/gitolite/install -to ~/local/gitolite \
&& echo 'export PATH="$HOME/local/gitolite:$PATH"' >> ~/.bashrc \
&& cp /tmp/mnt/home/git/local/gitolite/VREF/itpkg.sh ~/local/gitolite/VREF/itpkg.sh \
&& cd ~/local/gitolite/VREF \
&& sh itpkg.sh www.localhost.localdomain

USER deploy
ENV HOME /home/deploy


# nginx config files
RUN sudo sed -i '$i\    include itpkg/*.conf;'  /etc/nginx/nginx.conf \
&& sudo cp -r /tmp/mnt/etc/nginx/itpkg /etc/nginx/itpkg \
&& sudo chown -R deploy:deploy /etc/nginx/itpkg \
&& cd /tmp \
# htpasswd tools
&& wget https://aur.archlinux.org/packages/ap/apache-tools/apache-tools.tar.gz \
&& tar xf apache-tools.tar.gz \
&& cd apache-tools \
&& makepkg \
&& sudo pacman -U --noconfirm apache-tools-*-x86_64.pkg.tar.xz 


# bind9
RUN sudo abs extra/bind \
&& cp -r /var/abs/extra/bind /tmp \
&& cd /tmp/bind \
&& patch -p0 < /tmp/mnt/tmp/bind-mysql-geoip.patch \
&& makepkg \
&& sudo pacman -U --noconfirm bind-*-x86_64.pkg.tar.xz 

# docker registry
RUN virtualenv2 $HOME/local/python2 \
&& echo '. $HOME/local/python2/bin/activate' >> ~/.bashrc \
&& source $HOME/local/python2/bin/activate \
&& pip install docker-registry \
&& patch $(python -c 'import boto; import os; print os.path.dirname(boto.__file__)')/connection.py < /tmp/mnt/tmp/boto_header_patch.diff \
&& sudo cp /tmp/mnt/etc/{boto.cfg,docker-registry.yml} /etc 


# systemd
RUN sudo cp -v /tmp/mnt/etc/systemd/system/*.service /etc/systemd/system \
&& for s in sshd mysqld mongodb elasticsearch memcached redis docker-registry dovecot postfix named openvpn@server itpkg-bg itpkg-wss itpkg-www itpkg-setup nginx; \
do sudo systemctl enable $s; \
done
# clean
RUN sudo cp /tmp/mnt/root/chdomain /root/chdomain
RUN sudo rm -r /tmp/*


VOLUME ["/etc/nginx/itpkg", "$ITPKG_HOME/shared", "/home/git/repositories"]
EXPOSE 22 443 25 587 465 143 993 53/udp 1194/udp 9999
USER root

CMD ["/sbin/init"]

