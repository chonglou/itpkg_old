OBJS	= agent 
LDFLAGS = -ldflags "-s"

%: %.go
	go build $(LDFLAGS) -o $@ $<


all: $(OBJS)


clean: 
	-rm -f $(OBJS) *.key *.cert *.log *.cfg


format:
	gofmt -w *.go





