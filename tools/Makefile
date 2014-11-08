OBJS	= agent proxy
LDFLAGS = -ldflags "-s"

%: %.go
	go build $(LDFLAGS) -o $@ $<


all: $(OBJS)


clean: 
	-rm -f $(OBJS) *.key *.cert *.log


format:
	gofmt -w *.go





