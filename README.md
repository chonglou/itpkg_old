IT-PACKAGE
======================

## Deploy (Using docker)

### Install docker

More details at: [https://docs.docker.com/installation/](https://docs.docker.com/installation/)

#### For macos user
Download and install the latest release of [the Docker for OS X Installer](https://github.com/boot2docker/osx-installer/releases/latest)

    boot2docker init
    boot2docker start
    $(boot2docker shellinit)

#### For linux user

    # centos
    sudo rpm install docker
    sudo service docker start
    sudo chkconfig docker on

    # ubuntu
    sudo apt-get install docker.io

    # archlinux
    sudo pacman -S docker
    sudo pacman systemctl start docker
    sudo pacman systemctl enable docker

    # add user to docker group
    sudo gpasswd -a $(whoami) docker # Need re-login to make valid

### Running

#### Download
    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/arch/itpkg
    chmod +x itpkg

#### Start
First time to runing maybe take a long time(several minutes) to auto setup(generating ssl certs, random password, etc.), please wait.


    ./itpkg start

    # optional
    ./itpkg ssh # password: changeme
    tail -f /var/www/itpkg/install.log # see the install logging.

#### visit by your web brower

    https://www.localhost.localdomain # default user: root changeme
    https://mail.localhost.localdomain # need create a email account first.

#### visit by ssh

    ./itpkg ssh # password: changeme

#### change domain(after login by ssh)
    ./chdomain YOUR_DOMAIN
    

## That's ALL
Enjoy it!

## Contributing

1. Fork it ( https://github.com/chonglou/itpkg/fork )
2. Create your feature branch (`git checkout -b my-new-feature`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin my-new-feature`)
5. Create a new Pull Request

