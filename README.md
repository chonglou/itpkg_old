IT-PACKAGE
======================

## Deploy

### Using docker

#### Install docker

    # ubuntu
    sudo apt-get install docker.io
    # archlinux
    sudo pacman -S docker
    sudo pacman systemctl enable docker
    # add user to docker group
    sudo gpasswd -a $(whoami) docker # Need re-login to make valid

#### Setup

    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/arch/itpkg
    chmod +x itpkg

#### Running
First time to runing maybe take a long time to auto setup(generating ssl certs, random password, etc.), please wait.


    ./itpkg start

#### visit by your web brower

    https://www.localhost.localdomain # default user: root 12345678

#### visit by ssh

    ./itpkg ssh

#### change domain(after login by ssh)
    ./chdomain YOUR_DOMAIN
    

## That's ALL
Enjoy it!

