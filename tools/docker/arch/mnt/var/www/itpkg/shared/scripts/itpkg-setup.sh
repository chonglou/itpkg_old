#!/bin/sh

ITPKG_HOME=/var/www/itpkg
export ITPKG_HOME

init=$ITPKG_HOME/install.log

[ -f $init ] && exit 0

cd $ITPKG_HOME/shared/scripts/setup

for f in *.sh
do
	echo "### begin $f  $(date) ###" >> $init
	sh $f &>> $init
	echo "### end $f $(date) ###" >> $init
done
