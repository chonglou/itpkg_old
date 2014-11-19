IT-PACKAGE
======================

## Deploy

### Storage server

 * Install package

    sudo apt-get install mysql-server redis-server memcached

 *  Then create your database (run 'mysql -u root -p')

    CREATE DATABASE itpkg CHARACTER SET utf8;
    GRANT ALL PRIVILEGES ON itpkg.* TO 'itpkg'@'localhost' IDENTIFIED BY 'YOUR PASSWORD';


### Front server

 * Install packages

    sudo apt-get install docker.io

 * Setup
    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/itpkg
    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/mnt.tar.bz2
    tar xf mnt.tar.bz2
    vi mnt/nginx/itpkg.conf # NGINX SETTING
    cp YOUR_SSL.pem mnt/nginx/ssl
    vi mnt/shared/vars # SITE SETTING
    chmod +x itpkg

 * Running

    ./itpkg start

## That's ALL

 * You can open "https::YOUR_HOST" in browser or run "ssh -p 2222 root@localhost" in terminal console(default password is toor).

