package main

import (
	"encoding/hex"
	"encoding/json"
	//	"github.com/gorilla/websocket"
	"log"
	"os"
	"os/signal"
	"net/url"
	"fmt"
)

const version = "v20141124"

type State struct {
}

type Configuration struct {
	Origin  string   `json:"origin"`
	Host     string   `json:"host"`
	Port int `json:"port"`
	Ssl     bool     `json:"ssl"`
	Nid     string   `json:"nid"`
	Token   string   `json:"token"`
	Flags   []string `json:"flags"`
	Version string   `json:"version"`
}

func (cfg Configuration) Url string {
	var s string
	return fmt.Sprintf("%s:%s:%d", c.Ssl ? "wss":"ws", c.Host, c.Port)
}

func check_err(err error, msg string) {
	if err != nil {
		log.Fatalf(msg, err)
	}
}

func random_str(length int) string {
	f, err := os.OpenFile("/dev/urandom", os.O_RDONLY, 0)
	check_err(err, "No random device: %v")
	defer f.Close()
	b := make([]byte, length/2)
	f.Read(b)
	return hex.EncodeToString(b)
}

func write_status(state *State) {
	f, err := os.OpenFile("agent.status", os.O_RDWR|os.O_CREATE, 0600)
	defer f.Close()
	stateB, err := json.Marshal(state)
	check_err(err, "Generate json string error: %v")
	f.Write(stateB)
}

func load_config(filename string) *Configuration {
	var cfg *Configuration
	if _, err := os.Stat(filename); os.IsNotExist(err) {
		log.Printf("Cfg file %v not exist, will write a demo!", filename)
		f2, err := os.OpenFile(filename, os.O_RDWR|os.O_CREATE, 0600)
		defer f2.Close()
		cfg = &Configuration{
			Origin:  "http://localhost/",
			Host:     "localhost:9292",
			Port: 9292,
			Ssl:     false,
			Nid:     random_str(32),
			Token:   random_str(128),
			Flags:   []string{"monitor", "logging", "agent"},
			Version: version}
		cfgB, err := json.Marshal(cfg)
		check_err(err, "Generate json string error: %v")
		f2.Write(cfgB)
	} else {
		log.Printf("Load configuration from file %s", filename)
		f2, err := os.OpenFile(filename, os.O_RDONLY, 0)
		defer f2.Close()
		check_err(err, "Read configuration file error: %v")
		decoder := json.NewDecoder(f2)
		cfg = &Configuration{}
		err = decoder.Decode(cfg)
		check_err(err, "Parse json error: %v")
	}
	return cfg
}

func set_logger(filename string) *os.File {
	f, err := os.OpenFile(filename, os.O_RDWR|os.O_CREATE|os.O_APPEND, 0600)
	check_err(err, "")
	log.SetOutput(f)
	return f
}

func set_signal() {
	c := make(chan os.Signal, 1)
	signal.Notify(c, os.Interrupt)
	go func() {
		for sig := range c {
			log.Println("Exit [%v]!", sig)
		}
	}()
}

func main() {
	log_f := set_logger("agent.log")
	defer log_f.Close()
	set_signal()
	load_config("agent.cfg")
}

