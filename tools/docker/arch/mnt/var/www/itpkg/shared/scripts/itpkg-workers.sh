#!/bin/sh
. $(dirname $0)/ruby-env.sh
cd $ITPKG_HOME/current && bundle exec sidekiq -L $ITPKG_HOME/shared/log/workers.log -P $ITPKG_HOME/shared/tmp/pids/workers.pid -d
