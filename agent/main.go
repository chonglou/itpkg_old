package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"log"
	"log/syslog"
	"net"
	"os"
	"os/exec"
	"strings"
	"time"
)

type Config struct {
	Server string `json:"server"`
	Mysql  string `json:"mysql"`
	Serial string `json:"serial"`
	Key    string `json:"key"`
}

type Message struct {
	Act     string   `json:"act"`
	Data    []string `json:'data'`
	Created string   `json:'created'`
}

func main() {
	init_logger()
	log.Println("Begin...")

	config := load_config("config/agent.json")
	loop(config)

	log.Println("End...")
}

func loop(config Config) {
	log.Println(fmt.Sprintf("connect to: ", config.Server))

	client, err := net.Dial("tcp", config.Server)
	if err != nil {
		log.Println(fmt.Sprintf("connect server error: %v", err))
		return
	}
	defer client.Close()

	go line_reader(client, config)
	for {
		time.Sleep(1e9)
		send_line(client, "next", []string{})
		//time.Sleep(60e9)
		//send_line(client, "heart", []string{})
	}

}

func send_line(client net.Conn, act string, data []string) {
	b, err := json.Marshal(Message{Act: act, Data: data, Created: time.Now().Format("2006-01-02 15:04:05 -0700")})
	if err != nil {
		log.Println(err)
	}
	client.Write(b)
}

func line_reader(client net.Conn, config Config) {
	buf := make([]byte, 1024)
	for {
		n, err := client.Read(buf[:])
		if err != nil {
			log.Println(err)
			return
		}
		reqs := line_parse(string(buf[0 : n-1]))
		for _, req := range reqs {
			message_process(client, config, req)
		}
	}
}

func line_parse(line string) []Message {
	ss := strings.Split(line, "\n")
	rs := []Message{}
	for _, s := range ss {
		req := Message{}
		if err := json.Unmarshal([]byte(s), &req); err != nil {
			log.Println("Bad Format: ", line, err)
			break
		}
		rs = append(rs, req)
	}
	return rs
}

func message_process(client net.Conn, config Config, req Message) {
	switch req.Act {
	case "file":
		send_line(client, "file", run_file(req.Data))
	case "command":
		send_line(client, "command", run_command(req.Data))
	case "mysql":
		send_line(client, "mysql", run_mysql(config.Mysql, req.Data))
	case "login":
		send_line(client, "login", []string{config.Serial, config.Key})
		log.Println("Send login request")
	case "hi":
		log.Println("Login success")
	default:
		log.Println("Unknown: ", req)
	}
}

func run_file(lines []string) []string {
	return []string{}
}

func run_mysql(url string, lines []string) []string {
	return []string{}
}

func run_command(lines []string) []string {
	data := []string{}

	for _, line := range lines {
		cmd := exec.Command("sh", "-c", line)
		var out bytes.Buffer
		var stdout, stderr string
		cmd.Stdout = &out
		err := cmd.Run()
		if err != nil {
			stderr = err.Error()
		}
		stdout = out.String()
		data = append(data, stdout)
		data = append(data, stderr)
	}
	return data
}

func load_config(name string) Config {
	config := Config{}
	file, err := os.Open(name)
	if err != nil {
		log.Fatal(err)
	}
	decoder := json.NewDecoder(file)
	if err = decoder.Decode(&config); err != nil {
		log.Fatal(err)
	}
	return config
}

func init_logger() {
	logger, err := syslog.New(syslog.LOG_INFO, "test")
	if err == nil {
		log.SetOutput(logger)
	}
}
