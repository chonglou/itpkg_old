FROM base/archlinux:latest

#RUN echo 'ipv6.disable_ipv6=1' | sudo tee /etc/sysctl.d/40-ipv6.conf

# deploy user
RUN pacman -Syu --noconfirm
RUN useradd -s /bin/bash -m deploy
RUN passwd -l deploy
RUN pacman -S --noconfirm sudo
RUN echo 'deploy ALL=(ALL) NOPASSWD: ALL' > /etc/sudoers.d/deploy 

ENV HOME /home/deploy
ENV ITPKG_HOME /var/www/itpkg
RUN mkdir -p $ITPKG_HOME
RUN chown deploy:deploy $ITPKG_HOME
USER deploy

# ssh
RUN sudo pacman -S --noconfirm openssh
RUN sudo /usr/bin/ssh-keygen -A
RUN echo 'root:toor' | sudo chpasswd
#RUN sudo sed -i -e 's/^UsePAM yes/UsePAM no/g' /etc/ssh/sshd_config
RUN sudo sed -i "s/#PermitRootLogin/PermitRootLogin/g" /etc/ssh/sshd_config
RUN sudo systemctl enable sshd
RUN ssh-keygen -b 2048 -t rsa  -f $HOME/.ssh/id_rsa -q -N ""
RUN echo 'export PATH="$HOME/local/bin:$PATH"' >> ~/.bashrc
ENV PATH $HOME/local/bin:$PATH

# rbenv install
RUN sudo pacman -S --noconfirm --needed base-devel git 

RUN git clone https://github.com/sstephenson/rbenv.git ~/.rbenv
RUN git clone https://github.com/sstephenson/ruby-build.git ~/.rbenv/plugins/ruby-build
RUN git clone https://github.com/sstephenson/rbenv-vars.git ~/.rbenv/plugins/ruby-vars
RUN echo "export ITPKG_HOME=$ITPKG_HOME" >> ~/.bashrc
RUN echo 'export PATH="$HOME/.rbenv/bin:$PATH"' >> ~/.bashrc
RUN echo 'eval "$(rbenv init -)"' >> ~/.bashrc
RUN echo 'gem: --no-rdoc --no-ri' >> ~/.gemrc

ENV PATH $HOME/.rbenv/bin:$PATH
RUN eval "$(rbenv init -)"
RUN type rbenv
ENV RUBY_VERSION 2.1.4
ENV CONFIGURE_OPTS --disable-install-doc
RUN rbenv install $RUBY_VERSION
RUN rbenv global $RUBY_VERSION
ENV PATH $HOME/.rbenv/shims:$PATH
RUN gem install bundler
RUN rm -r /tmp/ruby-build*

# itpkg install
RUN sudo pacman -S --noconfirm nodejs libmariadbclient imagemagick cmake libgit2 pwgen
RUN git clone https://github.com/chonglou/itpkg.git --single-branch --branch master --depth 1 $ITPKG_HOME/current
RUN cd $ITPKG_HOME/current && git rev-parse --short HEAD > $ITPKG_HOME/current/REVISION
#RUN date "+%Y%m%d%H%M%S" > $ITPKG_HOME/current/REVISION
RUN cd $ITPKG_HOME/current && rm -r .git && bundle install --without test development
RUN mkdir -p $ITPKG_HOME/shared/log $ITPKG_HOME/shared/tmp
RUN mv $ITPKG_HOME/current/.rbenv-vars $ITPKG_HOME/shared/.rbenv-vars 
RUN sed -i 's/development/production/' $ITPKG_HOME/shared/.rbenv-vars 
RUN sed -i '/^ITPKG_DATABASE_PASSWORD/d' $ITPKG_HOME/shared/.rbenv-vars 
RUN cd $ITPKG_HOME/current && for i in $ITPKG_HOME/shared/* $ITPKG_HOME/shared/.[!.]*; do ln -sv $i $(basename $i); done

# gitolite
RUN cp ~/.ssh/id_rsa.pub /tmp
RUN sudo useradd -s /bin/bash -m tig
RUN sudo passwd -l tig
USER tig
ENV HOME /home/tig
#RUN ssh-keygen -b 2048 -t rsa  -f ~tig/.ssh/id_rsa -q -N ""
RUN mkdir -p ~tig/local/gitolite
RUN git clone https://github.com/sitaramc/gitolite.git --single-branch --branch master /tmp/gitolite
RUN /tmp/gitolite/install -to ~tig/local/gitolite
RUN echo 'export PATH="$HOME/local/gitolite:$PATH"' >> ~tig/.bashrc
RUN USER=deploy ~tig/local/gitolite/gitolite setup -pk /tmp/id_rsa.pub
RUN rm -r /tmp/gitolite
USER deploy
ENV HOME /home/deploy
RUN rm /tmp/id_rsa.pub

# nginx for itpkg
RUN sudo pacman -S --noconfirm nginx
RUN sudo systemctl enable nginx
RUN sudo sed -i '$i\    include itpkg/*.conf;'  /etc/nginx/nginx.conf
RUN sudo mkdir -p /etc/nginx/itpkg
RUN sudo chown -R deploy:deploy /etc/nginx/itpkg
RUN cd /etc/nginx/itpkg && openssl genrsa -out key.pem 2048 && openssl req -new -key key.pem  -subj "/C=US/ST=California/L=Goleta/O=itpkg/CN=itpkg.com" -out cert.csr -text && openssl x509 -req -in cert.csr -sha512 -days 3650  -signkey key.pem -out cert.pem -text
ADD nginx/www.conf /etc/nginx/itpkg/www.conf
ADD nginx/wss.conf /etc/nginx/itpkg/wss.conf
RUN sudo chown deploy:deploy /etc/nginx/itpkg/{www,wss}.conf
RUN cd /etc/nginx/itpkg && chmod 400 key.pem && chmod 444 cert.pem www.conf cert.csr

# runtime needed packages
RUN sudo pacman -S --noconfirm percona-server percona-server-clients redis memcached exim
RUN sudo systemctl enable mysqld 
RUN sudo systemctl enable memcached
RUN sudo systemctl enable redis
RUN sudo systemctl enable exim

# init data
RUN sudo mysqld_safe --user=mysql & \
sleep 10s && \
password=$(pwgen 16) && \
echo "ITPKG_DATABASE_PASSWORD=$password" >> $ITPKG_HOME/shared/.rbenv-vars && \
mysql -u root -h localhost -e "CREATE DATABASE itpkg CHARACTER SET utf8;GRANT ALL PRIVILEGES ON itpkg.* TO 'itpkg'@'localhost' IDENTIFIED BY '$password';FLUSH PRIVILEGES;" && \
cd $ITPKG_HOME/current && \
rake db:migrate && \
rake db:seed
RUN cd $ITPKG_HOME/current && rake assets:precompile
RUN mkdir -p $ITPKG_HOME/shared/tmp/{pids,sockets,storage}
ADD systemd/www.conf /etc/systemd/system/itpkg-www.service
ADD systemd/www.sh $ITPKG_HOME/shared/www.sh
ADD systemd/wss.conf /etc/systemd/system/itpkg-wss.service
ADD systemd/wss.sh $ITPKG_HOME/shared/wss.sh
ADD systemd/bg.conf /etc/systemd/system/itpkg-bg.service
ADD systemd/bg.sh $ITPKG_HOME/shared/bg.sh
RUN sudo chmod +x $ITPKG_HOME/shared/*.sh
RUN sudo systemctl enable itpkg-www
RUN sudo systemctl enable itpkg-wss
RUN sudo systemctl enable itpkg-bg

# other package
RUN sudo pacman -S --noconfirm s-nail net-tools dnsutils vim-minimal
RUN sudo pacman -R --noconfirm vi
RUN cd /usr/bin && sudo ln -sv vim vi
RUN cp /usr/share/vim/vim74/vimrc_example.vim ~/.vimrc
RUN echo "set nobackup" >> ~/.vimrc
RUN sudo cp ~/.vimrc /root/
RUN echo "alias ls='ls --color'" | sudo tee /root/.bash_profile

# clean
RUN sudo pacman -Scc --noconfirm


VOLUME ["/etc/nginx/itpkg", "$ITPKG_HOME/shared", "~tig/repositories"]
EXPOSE 22 25 443
USER root

CMD ["/sbin/init"]


