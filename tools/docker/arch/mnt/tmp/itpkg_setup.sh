#!/bin/sh
sudo mysqld_safe --user=mysql & 
sleep 10s 

password=$(pwgen 16) 

echo "ITPKG_DATABASE_PASSWORD=$password" >> $ITPKG_HOME/shared/.rbenv-vars 
mysql -u root -h localhost -e "CREATE DATABASE itpkg CHARACTER SET utf8;GRANT ALL PRIVILEGES ON itpkg.* TO 'itpkg'@'localhost' IDENTIFIED BY '$password';FLUSH PRIVILEGES;" 

cd $ITPKG_HOME/current 
rake db:migrate 
rake db:seed 
rake assets:precompile 

