#!/bin/sh
. $(dirname $0)/ruby-env.sh
cd $ITPKG_HOME/current && bundle exec rake -T journal:start
