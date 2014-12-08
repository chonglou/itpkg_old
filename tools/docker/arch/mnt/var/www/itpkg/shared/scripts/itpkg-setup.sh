#!/bin/sh

ITPKG_HOME=/var/www/itpkg
export ITPKG_HOME

init=$ITPKG_HOME/.init

[ -f $init ] && exit 0

cd $ITPKG_HOME/shared/scripts/setup

for f in *.sh
do
	sh $f
done

date >> $init
