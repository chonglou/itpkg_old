IT-PACKAGE
======================

## Deploy

### Storage server
    sudo apt-get install mysql-server redis-server memcached

### Front server
    sudo apt-get install docker.io
    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/itpkg
    wget https://raw.githubusercontent.com/chonglou/itpkg/master/tools/docker/mnt.tar.bz2
    tar xf mnt.tar.bz2
    vi mnt/nginx/itpkg.conf # NGINX SETTING
    cp YOUR_SSL.pem mnt/nginx/ssl
    vi mnt/shared/vars # SITE SETTING
    chmod +x itpkg
    ./itpkg start

## That's ALL

 * You can open "https::YOUR_HOST" in browser or run "ssh -p 2222 root@localhost" in terminal console(default password is toor).

