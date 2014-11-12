#!/bin/sh

sudo mkdir -p /var/www/itpkg/shared
sudo chown -R deploy:deploy /var/www/itpkg

sudo apt-get update
sudo apt-get -y install git build-essential libssl-dev
sudo apt-get clean

git clone https://github.com/sstephenson/rbenv.git ~/.rbenv
git clone https://github.com/sstephenson/ruby-build.git ~/.rbenv/plugins/ruby-build
git clone https://github.com/sstephenson/rbenv-vars.git ~/.rbenv/plugins/ruby-vars

cat >> ~/.bashrc <<EOF

export PATH=\$HOME/.rbenv/bin:\$PATH
eval "\$(rbenv init -)"

EOF


export PATH=$HOME/.rbenv/bin:$PATH
eval "$(rbenv init -)"

type rbenv

rbenv install 2.1.4
rbenv global 2.1.4
gem install bundler

