IT-PACKAGE
======================

## Deploy

### Storage server

 * Install package

    sudo apt-get install elasticsearch


### Front server

#### Install packages

    sudo apt-get install docker.io
    sudo gpasswd -a $(whoami) docker # Need re-login to make valid

#### Setup

    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/itpkg
    chmod +x itpkg

#### Running

    ./itpkg start

## That's ALL

#### visit by your web brower

    https::YOUR_HOST

#### visit by ssh

    ./itpkg ssh # password is toor.

