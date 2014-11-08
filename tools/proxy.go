package main

import (
	"crypto/rand"
	"crypto/rsa"
	"crypto/tls"
	"crypto/x509"
	"crypto/x509/pkix"
	"flag"
	"fmt"
	"io/ioutil"
	"log"
	"math/big"
	"net"
	"os"
	"time"
)

func checkError(msg string, err error) {
	if err != nil {
		log.Fatalf(msg, err)
	}
}

func main() {
	f, err := os.OpenFile("proxy.log", os.O_RDWR|os.O_CREATE|os.O_APPEND, 0600)
	checkError("error opening file: %v", err)
	defer f.Close()
	log.SetOutput(f)

	host := flag.String("host", "localhost", "Listen address")
	port := flag.Int("port", 11111, "Listen port")
	gen := flag.Bool("generate", false, "Generate certificate files")
	flag.Parse()

	if *gen {
		generateKeys()
		return
	}

	listen(*host, *port)

}

func listen(host string, port int) {
	ca_b, _ := ioutil.ReadFile("ca.cert")
	ca, _ := x509.ParseCertificate(ca_b)
	priv_b, _ := ioutil.ReadFile("ca.key")
	priv, _ := x509.ParsePKCS1PrivateKey(priv_b)

	pool := x509.NewCertPool()
	pool.AddCert(ca)

	cert := tls.Certificate{
		Certificate: [][]byte{ca_b},
		PrivateKey:  priv,
	}

	config := tls.Config{
		ClientAuth:   tls.RequireAndVerifyClientCert,
		Certificates: []tls.Certificate{cert},
		ClientCAs:    pool,
	}
	config.Rand = rand.Reader
	service := fmt.Sprintf("%s:%d", host, port)
	listener, err := tls.Listen("tcp", service, &config)
	checkError("fail to listen: %v", err)

	log.Println("listening", service)

	for {
		conn, err := listener.Accept()
		checkError("accept: %v", err)
		defer conn.Close()
		logConn("accept", conn, nil)
		go handle(conn)
	}
}

func logConn(msg string, conn net.Conn, err error) {
	log.Printf("[%s]: %s %v", conn.RemoteAddr(), msg, err)
}

func handle(conn net.Conn) {
	defer conn.Close()
	buf := make([]byte, 1024)
	for {
		n, err := conn.Read(buf)

		if err != nil {
			logConn("read", conn, err)
			break
		}

		tlscon, ok := conn.(*tls.Conn)
		if ok {
			state := tlscon.ConnectionState()
			sub := state.PeerCertificates[0].Subject
			log.Println(sub)
		}

		n, err = conn.Write(buf[:n])

		if err != nil {
			logConn("write", conn, err)
			break
		}
	}
	log.Println("closed", conn, nil)
}

func generateKeys() {

	ca := &x509.Certificate{
		SerialNumber: big.NewInt(1653),
		Subject: pkix.Name{
			Country:            []string{"US"},
			Organization:       []string{"itpkg"},
			OrganizationalUnit: []string{"ops"},
		},
		NotBefore:             time.Now(),
		NotAfter:              time.Now().AddDate(10, 0, 0),
		SubjectKeyId:          []byte{1, 2, 3, 4, 5},
		BasicConstraintsValid: true,
		IsCA:        true,
		ExtKeyUsage: []x509.ExtKeyUsage{x509.ExtKeyUsageClientAuth, x509.ExtKeyUsageServerAuth},
		KeyUsage:    x509.KeyUsageDigitalSignature | x509.KeyUsageCertSign,
	}

	priv, _ := rsa.GenerateKey(rand.Reader, 2048)
	pub := &priv.PublicKey
	ca_b, err := x509.CreateCertificate(rand.Reader, ca, ca, pub, priv)
	checkError("create ca keys failed: %v", err)

	ca_f := "ca.cert"
	log.Println("write to", ca_f)
	ioutil.WriteFile(ca_f, ca_b, 0400)

	priv_f := "ca.key"
	priv_b := x509.MarshalPKCS1PrivateKey(priv)
	log.Println("write to", priv_f)
	ioutil.WriteFile(priv_f, priv_b, 0400)

	cert2 := &x509.Certificate{
		SerialNumber: big.NewInt(1658),
		Subject: pkix.Name{
			Country:            []string{"US"},
			Organization:       []string{"itpkg"},
			OrganizationalUnit: []string{"ops"},
		},
		NotBefore:    time.Now(),
		NotAfter:     time.Now().AddDate(10, 0, 0),
		SubjectKeyId: []byte{1, 2, 3, 4, 6},
		ExtKeyUsage:  []x509.ExtKeyUsage{x509.ExtKeyUsageClientAuth, x509.ExtKeyUsageServerAuth},
		KeyUsage:     x509.KeyUsageDigitalSignature | x509.KeyUsageCertSign,
	}
	priv2, _ := rsa.GenerateKey(rand.Reader, 2048)
	pub2 := &priv2.PublicKey
	cert2_b, err2 := x509.CreateCertificate(rand.Reader, cert2, ca, pub2, priv)
	checkError("create client keys failed: %v", err2)

	cert2_f := "client.cert"
	log.Println("write to", cert2_f)
	ioutil.WriteFile(cert2_f, cert2_b, 0400)

	priv2_f := "client.key"
	priv2_b := x509.MarshalPKCS1PrivateKey(priv2)
	log.Println("write to", priv2_f)
	ioutil.WriteFile(priv2_f, priv2_b, 0400)

	ca_c, _ := x509.ParseCertificate(ca_b)
	cert2_c, _ := x509.ParseCertificate(cert2_b)

	err3 := cert2_c.CheckSignatureFrom(ca_c)
	log.Println("check signature", err3 == nil)
}
