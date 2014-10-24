package main

import (
	"encoding/csv"
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
	f, err := os.OpenFile(file("itpkg.log"), os.O_RDWR|os.O_CREATE|os.O_APPEND, 0600)
	if err != nil {
		log.Fatalf("error opening file: %v", err)
	}
	defer f.Close()
	log.SetOutput(f)

	log.Println("Startup!")
	cfg := read_cfg()
	cfg["aaa"] = Status{111, time.Now()}
	write_cfg(cfg)

	log.Println("Shutdown!")
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
