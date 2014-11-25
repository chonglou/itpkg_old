package main

import (
	"crypto/tls"
	"crypto/x509"
	"flag"
	"fmt"
	"io"
	"io/ioutil"
	"log"
	"os"
)

func checkError(msg string, err error) {
	if err != nil {
		log.Fatalf(msg, err)
	}
}

func main() {
	f, err := os.OpenFile("agent.log", os.O_RDWR|os.O_CREATE|os.O_APPEND, 0600)
	checkError("error opening file: %v", err)
	defer f.Close()
	log.SetOutput(f)

	host := flag.String("host", "data.itpkg.com", "WebSocket address")
	port := flag.Int("port", 11111, "WebSocket port")
	//uid := flag.String("uid", "null", "UID")
	verify := flag.Bool("verify", false, "Verify Certificate file")
	flag.Parse()

	loop(*host, *port, *verify)

}
func loop(host string, port int, verify bool) {
	cert_b, _ := ioutil.ReadFile("client.cert")
	priv_b, _ := ioutil.ReadFile("client.key")
	priv, _ := x509.ParsePKCS1PrivateKey(priv_b)

	cert := tls.Certificate{
		Certificate: [][]byte{cert_b},
		PrivateKey:  priv,
	}

	config := tls.Config{Certificates: []tls.Certificate{cert}, InsecureSkipVerify: !verify}
	conn, err := tls.Dial("tcp", fmt.Sprintf("%s:%d", host, port), &config)
	checkError("dial: %v", err)
	defer conn.Close()

	log.Println("connected to: ", conn.RemoteAddr())

	state := conn.ConnectionState()
	for _, v := range state.PeerCertificates {
		fmt.Println(x509.MarshalPKIXPublicKey(v.PublicKey))
		fmt.Println(v.Subject)
	}

	log.Println("handshake: ", state.HandshakeComplete)
	log.Println("mutual: ", state.NegotiatedProtocolIsMutual)

	message := "Hello Itpkg!"
	n, err := io.WriteString(conn, message)
	checkError("write: %v", err)
	log.Printf("client: wrote %q (%d bytes)", message, n)

	reply := make([]byte, 256)
	n, err = conn.Read(reply)
	log.Printf("client: read %q (%d bytes)", string(reply[:n]), n)
	log.Print("client: exiting")
}
