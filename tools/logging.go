package main

import (
	"code.google.com/p/go.net/websocket"
	"encoding/csv"
	"flag"
	"fmt"
	"github.com/ActiveState/tail"
	"io"
	"log"
	"os"
	"strconv"
	"time"
)

type Status struct {
	Index    int
	LastSync time.Time
}

func main() {
	host := flag.String("host", "data.itpkg.com", "WebSocket address")
	port := flag.Int("port", 9292, "WebSocket port")
	uid := flag.String("uid", nil, "UID")
	flag.Parse()
	files := flag.Args()

	if uid == nil {
		log.Fatalln("Need a uid")
	}

	f, err := os.OpenFile(file("itpkg.log"), os.O_RDWR|os.O_CREATE|os.O_APPEND, 0600)
	if err != nil {
		log.Fatalf("error opening file: %v", err)
	}
	defer f.Close()
	log.SetOutput(f)

	log.Println("Startup!")
	cfg := read_cfg()

	log.Printf("connect to %v:%v", *host, *port)
	log.Printf("moniting files: %v", files)

	for _, file := range files {
		_, ok := cfg[file]
		if !ok {
			cfg[file] = Status{-1, time.Now()}
		}
	}
	write_cfg(cfg)

	send_line(*host, *port, "aaa")

	log.Println("Shutdown!")
}

func send_line(host string, port int, line string) error {
	ws, err := websocket.Dial(fmt.Sprintf("ws://%s:%d/ws", host, port), "", fmt.Sprintf("http://%s/", host))
	if err != nil {
		log.Println("error in get: %v", err)
		return err
	}
	defer ws.Close()

	if _, err = ws.Write([]byte(line)); err != nil {
		log.Println("error in write: %v", err)
		return err
	}

	msg := make([]byte, 1024)
	var n int
	if n, err = ws.Read(msg); err != nil {
		log.Println("error in read: %v", err)
		return err
	}
	log.Printf("Received: %s. \n", msg[:n])
	return nil
}

func read_cfg() map[string]Status {
	cfg := make(map[string]Status)

	f, err := os.OpenFile(file("itpkg.cfg"), os.O_RDONLY, 0600)
	if err != nil {
		return cfg
	}
	defer f.Close()

	reader := csv.NewReader(f)
	for {
		record, err := reader.Read()
		if err == io.EOF {
			break
		} else if err != nil {
			log.Fatalf("error reading cfg: %v", err)
		}

		index, err1 := strconv.Atoi(record[1])
		if err1 != nil {
			log.Fatalf("error parse integer: %v", err1)
		}
		last_sync, err2 := time.Parse("2006-01-02 15:04:05.999999999 -0700 MST", record[2])
		if err2 != nil {
			log.Fatalf("error parse time: %v", err2)
		}
		cfg[record[0]] = Status{index, last_sync}
	}
	return cfg

}

func write_cfg(cfg map[string]Status) {
	f, err := os.OpenFile(file("itpkg.cfg"), os.O_RDWR|os.O_CREATE, 0600)
	if err != nil {
		log.Fatalf("error opening file: %v", err)
	}
	defer f.Close()

	writer := csv.NewWriter(f)
	for name, val := range cfg {
		err := writer.Write([]string{name, strconv.Itoa(val.Index), val.LastSync.String()})
		if err != nil {
			log.Fatalf("error write file: %v", err)
		}
	}
	writer.Flush()

}

func file(name string) string {
	dir := "tmp"
	os.MkdirAll(dir, 0700)
	return dir + "/" + name
}
