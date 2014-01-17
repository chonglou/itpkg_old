
target=/tmp/daemon

all:	
	mvn dependency:copy-dependencies
	mvn package
	-mkdir -pv ${target}/{etc,lib,bin,var}
	-cp manager.sh ${target}/
	-cp jsvc ${target}/bin
	-cp target/daemon.jar ${target}/itpkgd.jar
	-cp target/lib/* ${target}/lib
	-cp target/classes/{config.properties,logback.xml} ${target}/etc

clean:
	-rm -r ${target}
	mvn clean



