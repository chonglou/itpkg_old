#!/bin/sh
export PATH="$HOME/.rbenv/bin:$PATH"
eval "$(rbenv init -)"
cd /var/www/itpkg/current && puma -e production -b unix:///var/www/itpkg/shared/tmp/sockets/puma.sock --pidfile /var/www/itpkg/shared/tmp/pids/puma.pid -d
