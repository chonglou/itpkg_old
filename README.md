IT-PACKAGE
=====

## Env
  mysql, redis, pwgen, openssl, ssh, net-snmp, openfire, git

## Deploy
### Add deploy user
    useradd -s /bin/bash -m deploy
    passwd deploy

### Setup ssh login by key file.
    cat /tmp/id_rsa.pub >> .ssh/authorized_keys # /tmp/id_rsa.pub is your public key file

### Test ssh login
    ssh deploy@YOUR_HOME # RUN ON LOCAL

### Install rbenv
    git clone https://github.com/sstephenson/rbenv.git ~/.rbenv
    git clone https://github.com/sstephenson/ruby-build.git ~/.rbenv/plugins/ruby-build
    git clone https://github.com/sstephenson/rbenv-vars.git ~/.rbenv/plugins/ruby-vars

### Add to ~/.bashrc
    export PATH="$HOME/.rbenv/bin:$PATH"
    eval "$(rbenv init -)"

### Test rbenv (need relogin first)
    type rbenv

### Install ruby
    rbenv install 2.1.3
    rbenv global 2.1.3
    gem install bundler
    rbenv rehash

### Shell Vars
    rake itpkg:env # RUN ON LOCAL
    # COPY OUTPUT LINES TO /var/www/shared/.rbenv-vars

### Deolpying (RUN ON LOCAL)
    vi config/deploy/production.rb # setup user and server
    cap production deploy:check # check config file
    cap production deploy

## Usage

### Setup
    rake itpkg:env # then add output lines to ~/.bashrc

### Start

#### WebSite

    puma -C config/web.cfg

#### WebSocket

    puma -C config/wss.cfg wss.ru

#### Background Worker

    bundle exec sidekiq -d # start new worker
    ps -ef | grep sidekiq | grep -v grep | awk '{print $2}' | xargs kill -USR1 # stop all workers

#### Front Node

    cd tools; make
    ./logging -h example.com -p 9292 /var/log/syslog /var/log/snmpd.log # logging

### Restart

    kill -SIGUSR2 `cat tmp/pids/wss.pid`
    kill -SIGUSR2 `cat tmp/pids/web.pid`
