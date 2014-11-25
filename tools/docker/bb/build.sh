#!/bin/sh

CURDIR=`pwd`
BUSYBOX_VERSION=1.22.1
WORKDIR=$CURDIR/tmp/build
BUSYBOX=$WORKDIR/busybox/busybox
ROOTFS=$CURDIR/tmp/rootfs
IMAGE_NAME=chonglou/itpkg

if [ ! -f "$BUSYBOX" ]; then
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

[ ! -f "$BUSYBOX" ] && exit 1
ldd "$BUSYBOX" && echo 'error: '$BUSYBOX' appears to be a dynamic executable' && exit 1

[ -d $ROOTFS ] &&  rm -r $ROOTFS
mkdir -pv $ROOTFS
cd $ROOTFS

mkdir bin etc dev dev/pts lib proc sys tmp
chmod 777 tmp
touch etc/resolv.conf
cp /etc/nsswitch.conf etc/nsswitch.conf
echo root:x:0:0:root:/:/bin/sh > etc/passwd
echo root:x:0: > etc/group
ln -s lib lib64
ln -s bin sbin
cp "$BUSYBOX" bin
for X in $(bin/busybox --list)
do
	ln -s busybox bin/$X
done
rm bin/init
ln bin/busybox bin/init
cp /usr/lib/lib{pthread,c,dl,nsl,nss_*}.so.* lib
cp /lib64/ld-linux-x86-64.so.2 lib
for X in console null ptmx random stdin stdout stderr tty urandom zero
do
	sudo cp -a /dev/$X dev
done



