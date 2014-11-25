#!/bin/sh
export PATH="$HOME/.rbenv/bin:$PATH"
eval "$(rbenv init -)"
cd /var/www/itpkg/current && bundle exec sidekiq -e production -L /var/www/itpkg/shared/log/bg.log -P /var/www/itpkg/shared/tmp/pids/bg.pid -d
