package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"log/syslog"
	"os"
	"os/exec"
	"strings"
	"time"
)

type AgentCfg struct {
	Serial string `json:"serial"`
	Key    string `json:"key"`
}

type MysqlCfg struct {
	User     string `json:"user"`
	Password string `json:"password"`
}

type Config struct {
	Server string   `json:"server"`
	Mysql  MysqlCfg `json:"mysql"`
	Agent  AgentCfg `json:"agent"`
}

type Message struct {
	Data    []string `json:"data"`
	Created string   `json:"created"`
}

func main() {
	init_logger()
	log.Println("Begin...")

	config, err := load_config("config/agent.json")
	if err == nil {
		err = loop(config)
	}
	log.Println(err)
	log.Println("End...")
	time.Sleep(1e6)
}

func load_config(name string) (Config, error) {
	config := Config{}
	file, err := os.Open(name)
	if err == nil {
		err = json.NewDecoder(file).Decode(&config)
	}
	return config, err
}

func init_logger() {
	logger, err := syslog.New(syslog.LOG_DEBUG, "itpkg-agent")
	if err == nil {
		log.SetOutput(logger)
	}
}

func loop(config Config) error {
	var msg Message
	var err error
	var ws *websocket.Conn

	ws, _, err = websocket.DefaultDialer.Dial(config.Server, nil)
	if err == nil {
		defer ws.Close()

		err = send(ws, []string{"login", config.Agent.Serial, config.Agent.Key})
		if err == nil {
			for {
				msg, err = receive(ws)
				lines := msg.Data
				if err == nil {
					switch lines[0] {
					case "shell":
						err = send(ws, run_shell(lines[1:]))
					case "file":
						err = send(ws, run_file(lines[1], lines[2], lines[3], lines[4:]))
					case "mysql":
						err = send(ws, run_mysql(config.Mysql.User, config.Mysql.Password, lines[1:]))
					case "ok":
						log.Println("Ok: ", lines[1:])
					case "fail":
						log.Println("Fail: ", lines[1:])
					case "hello":
						log.Println("Connect success")
					case "bye":
						log.Println("Disconnect")
						break
					default:
						log.Println("Unknown message: ", lines[1:])
					}
				}
				if err != nil {
					log.Println(err)
					break
				}
			}
		}
	}
	return err

}

func send(ws *websocket.Conn, data []string) error {
	msg, err := json.Marshal(Message{Data: data, Created: time.Now().Format("2006-01-02 15:04:05 -0700")})
	if err == nil {
		err = ws.WriteMessage(websocket.TextMessage, msg)
	}
	return err
}

func receive(ws *websocket.Conn) (Message, error) {
	_, buf, err := ws.ReadMessage()
	msg := Message{}
	if err == nil {
		err = json.Unmarshal(buf, &msg)
	}
	return msg, err

}

func run_file(name, owner, mode string, lines []string) []string {
	return run_shell([]string{fmt.Sprintf("cat <<EOF > %s\n%s\nEOF", name, strings.Join(lines, "\n")),
		fmt.Sprintf("chown %s %s", owner, name),
		fmt.Sprintf("chmod %s %s", mode, name)})
}

func run_mysql(user, password string, lines []string) []string {
	return run_shell([]string{fmt.Sprintf("mysql -u %s \"-p%s\" -e \"%s;\"", user, password, strings.Join(lines, ";"))})
}

func run_shell(lines []string) []string {
	var stdout, stderr []string
	for _, line := range lines {
		cmd := exec.Command("sh", "-c", line)
		cmd.Stdin = strings.NewReader("some input")
		var _out, _err bytes.Buffer
		cmd.Stdout = &_out
		cmd.Stderr = &_err
		if err := cmd.Run(); err != nil {
			log.Println("[", line, "]:", err)
		}
		stdout = append(stdout, _out.String())
		stderr = append(stderr, _err.String())
	}
	return []string{"response", strings.Join(stdout, "\n"), strings.Join(stderr, "\n")}
}
