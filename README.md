IT-PACKAGE
=====

## Env
 * mysql, redis, pwgen, openssl, ssh, net-snmp

## Usage

### Setup
    rake itpkg:env # then add output lines to ~/.bashrc

### Start

 * WebSite

    puma -C config/web.cfg

 * WebSocket

    puma -C config/wss.cfg wss.ru

 * Background Worker

    bundle exec sidekiq -d # start new worker
    ps -ef | grep sidekiq | grep -v grep | awk '{print $2}' | xargs kill -USR1 # stop all workers

 * Front Node

    cd tools; make
    ./logging -h example.com -p 9292 /var/log/syslog /var/log/snmpd.log # logging

### Restart

 * kill -SIGUSR2 `cat tmp/pids/wss.pid`
 * kill -SIGUSR2 `cat tmp/pids/web.pid`
