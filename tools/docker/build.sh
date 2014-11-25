#!/bin/sh

BUSYBOX_VERSION=1.22.1
WORKDIR=tmp/build
CURDIR=`pwd`
busybox=$WORKDIR/busybox/busybox
ROOTFS=tmp/rootfs

if [ -z "$busybox" ]; then
	mkdir -pv $WORKDIR
	cd $WORKDIR

	if [ ! -L busybox ]
	then
		wget http://busybox.net/downloads/busybox-$BUSYBOX_VERSION.tar.bz2
		tar xf busybox-$BUSYBOX_VERSION.tar.bz2
		ln -sv busybox-$BUSYBOX_VERSION busybox
	fi
	cd busybox
	[ -f .config ] || make menuconfig
	cp -v .config $CURDIR/config
	make clean
	make -j 4
fi

[ ! -f "$busybox" ] && exit 1
ldd "$busybox" && echo 'error: '$busybox' appears to be a dynamic executable' && exit 1

[ -d $ROOTFS ] && rm -r $ROOTFS
mkdir -pv $ROOTFS/bin
cp -v $busybox $ROOTFS/bin/busybox

(
	cd $ROOTFS
	IFS=$'\n'
	modules=( $(bin/busybox --list-modules) )
	unset IFS
	for module in "${modules[@]}"; do
		mkdir -p "$(dirname "$module")"
		ln -sf /bin/busybox "$module"
	done
)

