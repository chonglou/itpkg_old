IT-PACKAGE
=====

### Env
 * mysql, redis, pwgen, openssl, ssh, net-snmp

### Usage

#### Setup
    rake itpkg:env

#### Background Worker
    bundle exec sidekiq -d # start new worker
    ps -ef | grep sidekiq | grep -v grep | awk '{print $2}' | xargs kill -USR1 # stop all workers

#### Front Node
    cd tools; make
    ./logging -h example.com -p 10001 /var/log/syslog /var/log/snmpd.log
