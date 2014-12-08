#!/bin/sh

ITPKG_HOME=/var/www/itpkg
PATH="$HOME/.rbenv/bin:$PATH"
export ITPKG_HOME PATH

eval "$(rbenv init -)"

cd $ITPKG_HOME/current && bundle exec sidekiq -e production -L $ITPKG_HOME/shared/log/bg.log -P $ITPKG_HOME/shared/tmp/pids/bg.pid -d
