
all:
	cd core;mvn clean; mvn install
	cd daemon;mvn clean; mvn package
	cd admin;mvn clean; mvn package


clean:
	cd core;mvn clean
	cd daemon;mvn clean
	cd admin;mvn clean


