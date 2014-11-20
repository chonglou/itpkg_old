IT-PACKAGE
=====

## Clone source(RUN ON LOCAL)
    git clone https://github.com/chonglou/itpkg.git
    cd itpkg
    
## Setting up deploy user(NEEDS TO BE DONE ON EVERY SERVER IN YOUR ENVIRONMENT)

### Create deploy user (RUN AS ROOT)
    useradd -s /bin/bash -m deploy
    passwd -l deploy # locks user
    mkdir -p ~deploy/.ssh
    chown deploy:deploy ~deploy/.ssh
    chmod 700 ~deploy/.ssh
    
#### Setup sudo (OPTIONAL):
Add to file /etc/sudoers.d/deploy
     
    deploy ALL=(ALL) NOPASSWD: ALL

### Upload your public key(RUN ON LOCAL)
    ssh-keygen -t rsa # ONLY RUN IF YOU DIDN'T HAVE SSH KEYS
    scp ~/.ssh/id_rsa.pub deploy@YOUR_HOST:/tmp # id_rsa.pub is your public key file 

### Setup ssh login by key file.
    cat /tmp/id_rsa.pub >> .ssh/authorized_keys 

### Test ssh login(RUN ON LOCAL)
    ssh deploy@YOUR_HOST 
    
### Setup ruby env
    ssh deploy@YOUR_HOST 'bash -s' < tools/init.sh

## Deploy

### Storage

### Install needed package
    sudo apt-get update
    sudo apt-get -y install git libgit2-dev build-essential cmake pkg-config openssl libssl-dev mysql-server libmysqlclient-dev nginx nodejs  
    sudo apt-get clean

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
    rbenv install 2.1.4
    rbenv global 2.1.4
    gem install bundler
    rbenv rehash

### MySQL(run 'mysql -u root -p')
    CREATE DATABASE itpkg CHARACTER SET utf8;
    GRANT ALL PRIVILEGES ON itpkg.* TO 'itpkg'@'localhost' IDENTIFIED BY 'YOUR PASSWORD';
    FLUSH PRIVILEGES;

### Setup deploy directory
    sudo mkdir -p /var/www/itpkg/shared
    sudo chown -R deploy:deploy /var/www/itpkg
   

### Shell Vars
    rake itpkg:env # RUN ON LOCAL
    # COPY OUTPUT LINES TO /var/www/shared/.rbenv-vars

### Deolpying (RUN ON LOCAL)
    vi config/deploy/production.rb # setup user and server
    cap production deploy:check # check config file
    cap production deploy
    cap production db:seed # ONLY NEED RUN ON FIRST TIME

### Setup nginx

 * Generate https certs (RUN ON LOCAL) or upload your's files
    
    rake nginx:ssl
    scp tmp/storage/ssl/web/cert.pem deploy@YOUR_HOST:/tmp
    scp tmp/storage/ssl/web/key.pem deploy@YOUR_HOST:/tmp

 * Setup certs

    sudo mkdir -p /etc/nginx/ssl
    cd /etc/nginx/ssl
    sudo cp /tmp/cert.pem itpkg-cert.pem
    sudo chmod 444 itpkg-cert.pem
    sudo cp /tmp/key.pem itpkg-key.pem
    sudo chmod 400 itpkg-key.pem
    rm /tmp/cert.pem /tmp/key.pem
    

 * Upload nginx files(RUN ON LOCAL)
    
    cap production nginx:setup
    cap production nginx:reload

## Usage

### Others

#### Docker
    cap production docker:install # install docker
    cap production docker:start # start docker service
    cap production docker:stop # stop docker service
    cap production docker:status # docker service status

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