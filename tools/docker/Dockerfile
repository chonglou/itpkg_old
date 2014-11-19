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
RUN sudo sed -i -e 's/^UsePAM yes/UsePAM no/g' /etc/ssh/sshd_config
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
RUN git clone https://github.com/chonglou/itpkg.git --single-branch --branch master --depth 1 $ITPKG_HOME/current
RUN sudo pacman -S --noconfirm nodejs libmariadbclient imagemagick cmake libgit2
RUN cd $ITPKG_HOME/current && rm -r .git .rbenv-vars log && bundle install --without test development
RUN cd $ITPKG_HOME/current && rm -r log && ln -s ../shared/log log && ln -s ../shared/tmp tmp && ln -s ../shared/.rbenv-vars .rbenv-vars

# nginx for itpkg
RUN sudo pacman -S --noconfirm nginx
RUN sudo sed -i '$i\    include itpkg.conf;'  /etc/nginx/nginx.conf
RUN sudo ln -s /mnt/nginx/itpkg.conf /etc/nginx
RUN sudo ln -s /mnt/nginx/ssl /etc/nginx
RUN sudo systemctl enable nginx

# gitolite
mkdir -p $HOME/local/gitolite
git clone https://github.com/sitaramc/gitolite.git --single-branch --branch master /tmp/gitolite
/tmp/gitolite/install -to $HOME/local/gitolite
RUN echo 'export PATH="$HOME/local/gitolite:$PATH"' >> ~/.bashrc
ENV PATH $HOME/gitolite:$PATH
gitolite setup -pk $HOME/.ssh/id_rsa.pub
git clone localhost:gitolite-admin $ITPKG_HOME/current/shared/gitolite-admin


# other packages
RUN sudo pacman -S --noconfirm net-tools

# clean
RUN sudo pacman -Scc --noconfirm


VOLUME [ "/sys/fs/cgroup" "/etc/nginx/itpkg" "$ITPKG_HOME/shared"]
EXPOSE 22 443
USER root

CMD ["/sbin/init"]


