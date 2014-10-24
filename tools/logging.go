package main

import (
	"code.google.com/p/go.net/websocket"
	"encoding/json"
	"flag"
	"fmt"
	"github.com/ActiveState/tail"
	"log"
	"os"
	"time"
)

type Request struct {
	Uid     string    `json:"uid"`
	Type    string    `json:"type"`
	Version int       `json:"version"`
	Created time.Time `json:"created"`

	Filename string `json:"filename"`
	Line     string `json:"line"`
}

func main() {
	host := flag.String("host", "data.itpkg.com", "WebSocket address")
	port := flag.Int("port", 9292, "WebSocket port")
	uid := flag.String("uid", "null", "UID")
	flag.Parse()
	files := flag.Args()

	if *uid == "null" {
		log.Fatalln("Need client uid.")
	}
	if len(files) == 0 {
		log.Fatalln("Need file list.")
	}

	f, err := os.OpenFile(file("itpkg.log"), os.O_RDWR|os.O_CREATE|os.O_APPEND, 0600)
	if err != nil {
		log.Fatalf("error opening file: %v", err)
	}
	defer f.Close()
	log.SetOutput(f)

	log.Println("Startup!")
	log.Printf("connect to %v:%v", *host, *port)
	log.Printf("moniting files: %v", files)

	channel := make(chan string)
	for _, fn := range files {
		go watch(channel, *uid, fn)
	}

	loop(channel, *host, *port)

	log.Println("Shutdown!")
}

func watch(channel chan string, uid string, filename string) {
	t, err := tail.TailFile(filename, tail.Config{Follow: true, Location: &tail.SeekInfo{0, os.SEEK_END}})
	if err != nil {
		log.Fatalf("error in watch: %v", err)
	}
	for line := range t.Lines {
		j, _ := json.Marshal(&Request{Uid: uid, Version: 0x01, Type: "logging", Created: time.Now(), Filename: filename, Line: line.Text})
		channel <- string(j)
	}

}

func loop(channel chan string, host string, port int) {
	ws, err := websocket.Dial(fmt.Sprintf("ws://%s:%d/ws", host, port), "", fmt.Sprintf("http://%s/", host))
	if err != nil {
		log.Fatalf("error in get: %v", err)
	}
	defer ws.Close()

	for line := range channel {
		if _, err = ws.Write([]byte(line)); err != nil {
			log.Fatalf("error in write: %v", err)
		}

		msg := make([]byte, 512)
		var n int
		if n, err = ws.Read(msg); err == nil {
			log.Printf("received: %s. \n", msg[:n])
		} else {
			log.Println("error in read: %v", err)
		}
	}

}

func file(name string) string {
	dir := "tmp"
	os.MkdirAll(dir, 0700)
	return dir + "/" + name
}
