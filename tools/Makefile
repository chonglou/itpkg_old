BIN	= bin
OBJS	= $(addprefix $(BIN)/, logging monitor)
LDFLAGS = -ldflags "-s"

$(BIN)/%: %.go
	go build $(LDFLAGS) -o $@ $<


all: check $(OBJS)



clean: 
	if [ -d $(BIN) ]; then rm -rv $(BIN); fi


check:
	if [ ! -d $(BIN) ]; then mkdir -pv $(BIN); fi


format:
	gofmt -w *.go



