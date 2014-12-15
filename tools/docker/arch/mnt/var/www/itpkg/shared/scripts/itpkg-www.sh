#!/bin/sh
. $(dirname $0)/ruby-env.sh
cd $ITPKG_HOME/current && bundle exec puma -b unix://$ITPKG_HOME/shared/tmp/sockets/www.sock --pidfile $ITPKG_HOME/shared/tmp/pids/www.pid -d config.ru
