p=/com/odong/itpkg
t=router.tar.bz2

all:
	mvn clean
	mvn package


router:
	-rm -rv /tmp/com /tmp/${t}
	-mkdir -pv /tmp${p}
	-cp -rv src/main/java${p}/router /tmp${p}
	cd /tmp;tar jcvf ${t} com


