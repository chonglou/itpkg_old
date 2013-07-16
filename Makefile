proto_dir=./core/src/main/java

all: 
	cd core;mvn clean; mvn install
	cd daemon;mvn clean; mvn package
	cd admin;mvn clean; mvn package


clean:
	cd core;mvn clean
	cd daemon;mvn clean
	cd admin;mvn clean


proto: tools/rpc.proto
	protoc --java_out=${proto_dir} tools/rpc.proto



