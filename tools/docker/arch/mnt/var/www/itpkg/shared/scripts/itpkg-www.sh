#!/bin/sh

ITPKG_HOME=/var/www/itpkg
PATH="$HOME/.rbenv/bin:$PATH"
export ITPKG_HOME PATH

eval "$(rbenv init -)"

cd $ITPKG_HOME/current && bundle exec puma -e production -b unix://$ITPKG_HOME/shared/tmp/sockets/www.sock --pidfile $ITPKG_HOME/shared/tmp/pids/www.pid -d config.ru
