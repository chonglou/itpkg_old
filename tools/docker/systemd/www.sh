#!/bin/sh
export PATH="$HOME/.rbenv/bin:$PATH"
eval "$(rbenv init -)"
cd /var/www/itpkg/current && bundle exec puma -e production -b unix:///var/www/itpkg/shared/tmp/sockets/www.sock --pidfile /var/www/itpkg/shared/tmp/pids/www.pid -d config.ru
