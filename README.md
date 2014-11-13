IT-PACKAGE
======================

## Deploy

### Storage server
    sudo apt-get install mysql-server redis-server memcached

### Front server
    sudo apt-get install docker.io
    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/itpkg
    ./itpkg setup
    ./itpkg start
  

### Install needed packages
    sudo apt-get install git docker.io
    apt-get install git docker.io
    git clone https://github.com/chonglou/itpkg.git
    cd chonglou
