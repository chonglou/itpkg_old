#!/bin/sh
export CONFIGURE_OPTS=" --disable-install-doc"

git clone https://github.com/sstephenson/rbenv.git ~/.rbenv
git clone https://github.com/sstephenson/ruby-build.git ~/.rbenv/plugins/ruby-build
git clone https://github.com/sstephenson/rbenv-vars.git ~/.rbenv/plugins/ruby-vars

cat >> ~/.bashrc <<EOF
export PATH=\$HOME/.rbenv/bin:\$PATH
eval "\$(rbenv init -)"
EOF
echo 'gem: --no-rdoc --no-ri' >> ~/.gemrc

export PATH=$HOME/.rbenv/bin:$PATH
eval "$(rbenv init -)"

type rbenv
rbenv install $RUBY_VERSION
rbenv global $RUBY_VERSION
gem install bundler

git clone https://github.com/chonglou/itpkg.git --single-branch --branch master --depth 1 $ITPKG_HOME
cd $ITPKG_HOME
rm -r .git
bundle install

rm -r /tmp/ruby-build*

