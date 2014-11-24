#!/bin/sh
BUILDROOT_VERSION=2014.08
WORKDIR=$HOME/build

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
	ln -svf buildroot-$BUILDROOT_VERSION buildroot
	
	#cd $WORKDIR/buildroot
	#git pull
	#git clone git://git.buildroot.net/buildroot --single-branch --branch master --depth 1 $WORKDIR/buildroot
fi

if [ ! -f $WORKDIR/buildroot/output/images/rootfs.tar ]
then
	#cp -v .config $WORKDIR/buildroot
	cd $WORKDIR/buildroot
	make menuconfig
	make -j 4
	ls -lh output/images/rootfs.tar
fi

if [ ! -f $WORKDIR/buildroot/output/images/fixup.tar ]
then
	cd $WORKDIR/buildroot/output/images
	mkdir extra extra/etc extra/sbin extra/lib extra/lib64
	touch extra/etc/resolv.conf
	touch extra/sbin/init
	ldd $(which docker)
	cp -v /usr/lib/{libdl-2.20.so,libdl-2.20.so,libpthread.so.0,libpthread-2.20.so,libc.so.6,libc-2.20.so} extra/lib
	ls -lh extra/lib
	cp -v /lib64/ld-linux-x86-64.so.2 extra/lib64
	cp rootfs.tar -v fixup.tar
	tar rvf fixup.tar -C extra .
fi

# docker test
cd $WORKDIR/buildroot/output/images
docker import - itpkg < fixup.tar
docker run -t -i itpkg /bin/sh
echo 'Done'

