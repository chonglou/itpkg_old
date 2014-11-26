OBJS	= agent 
LDFLAGS = -ldflags "-s"

%: %.go
	go build $(LDFLAGS) -o $@ $< protocols.pb.go


all: $(OBJS)


clean: 
	-rm -f $(OBJS) *.key *.cert *.log *.cfg

get:
	go get -u github.com/golang/protobuf/{proto,protoc-gen-go}
	go get -tags zmq_4_x github.com/alecthomas/gozmq

format:
	gofmt -w *.go





