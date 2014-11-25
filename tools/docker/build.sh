#!/bin/sh
CURDIR=`pwd`
BUILDROOT_VERSION=2014.08
WORKDIR=$HOME/build
IMAGE_NAME=itpkg

if [ ! -d $WORKDIR ]
then
	mkdir -pv $WORKDIR
fi

# build tools
if [ ! -L $WORKDIR/buildroot ]
then
	cd $WORKDIR
	[ -f buildroot-$BUILDROOT_VERSION.tar.bz2 ] || wget http://buildroot.uclibc.org/downloads/buildroot-$BUILDROOT_VERSION.tar.bz2
	tar xvf buildroot-$BUILDROOT_VERSION.tar.bz2
	ln -sv buildroot-$BUILDROOT_VERSION buildroot
	
	#cd $WORKDIR/buildroot
	#git pull
	#git clone git://git.buildroot.net/buildroot --single-branch --branch master --depth 1 $WORKDIR/buildroot

fi

cd $WORKDIR/buildroot
[ -f .config ] || cp -v $CURDIR/config .config
make -j 4
ls -lh output/images/rootfs.tar

[ -f $WORKDIR/buildroot/output/images/rootfs.tar ] && cp -v $WORKDIR/buildroot/output/images/rootfs.tar  $CURDIR/rootfs.tar

echo 'Done'

