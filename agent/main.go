package main

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"net"
	"os"
	"time"
	"bytes"
	"os/exec"
)

type Config struct {
	Server string `json:"server"`
	Port   int    `json:"port"`
	Serial string `json:"serial"`
	Key    string `json:"key"`
}

type Response struct {
	Stderr string `json:"string"`
	Stdout string `json:"string"`
	Errno  int    `json:"errno"`
}

func main() {
	//init_logger("tmp")
	log.Println("Begin...")
	//config := load_config("config/agent.json")
	//loop(config.Server, config.Port)
	log.Println(run_command("/bin/ls -l /tmp"))
	log.Println(run_command("/bin/ls -l /tmp1"))

	log.Println("End...")
}

func run_command(app string) Response {
	resp := Response{}
	cmd := exec.Command("sh", "-c", app)
	var out bytes.Buffer
	cmd.Stdout = &out
	err := cmd.Run()
	if err != nil {
		resp.Stderr = err.Error()
	}
	resp.Stdout = out.String()
	return resp
}

func line_reader(r io.Reader) {
	buf := make([]byte, 1024)
	for {
		n, err := r.Read(buf[:])
		if err != nil {
			log.Println("error on read from server: %v", err)
			return
		}
		request := string(buf[0:n])
		log.Println("GET: " + request)
	}
}

func loop(server string, port int) {

	host := fmt.Sprintf("%s:%d", server, port)
	log.Println("connect to: ", host)
	client, err := net.Dial("tcp", host)
	if err != nil {
		log.Println("connect server error: %v", err)
		return
	}
	defer client.Close()

	go line_reader(client)

	for {
		msg := "ls -l /tmp\n"
		_, err := client.Write([]byte(msg))
		if err != nil {
			log.Println("send data error: %v", err)
			break
		}

		time.Sleep(1e9)
	}
}

func load_config(name string) Config {
	log.Println("Load config from ", name)
	file, err1 := os.Open(name)
	if err1 != nil {
		log.Fatalf("error opening config file: %v", err1)
	}
	decoder := json.NewDecoder(file)
	config := Config{}
	err2 := decoder.Decode(&config)
	if err2 != nil {
		log.Fatalf("error reading config file: %v", err2)
	}
	return config
}

func init_logger(path string) {
	f, err := os.OpenFile(fmt.Sprintf("%s/%s", path, time.Now().Format("2006-01-02")), os.O_RDWR|os.O_CREATE|os.O_APPEND, 0600)
	if err != nil {
		log.Fatalf("error opening log file: %v", err)
	}
	//	defer f.Close()
	log.SetOutput(f)
}
