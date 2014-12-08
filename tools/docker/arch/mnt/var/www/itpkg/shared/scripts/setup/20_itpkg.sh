#!/bin/sh

password=$(pwgen 16) 

mysql -u root -h localhost -e "CREATE DATABASE itpkg CHARACTER SET utf8;GRANT ALL PRIVILEGES ON itpkg.* TO 'itpkg'@'localhost' IDENTIFIED BY '$password';FLUSH PRIVILEGES;" 
sed -i "s/^ITPKG_DATABASE_PASSWORD=.*/ITPKG_DATABASE_PASSWORD=$password/g" $ITPKG_HOME/shared/.rbenv-vars

for i in SECRET_KEY_BASE DEVISE_SECRET_KEY PASSWORD
do
	sed -i "s/^ITPKG_$i=.*/ITPKG_$i=$(pwgen 128)/g" $ITPKG_HOME/shared/.rbenv-vars
done

su deploy<<'EOF'
export PATH="$HOME/.rbenv/bin:$PATH"
eval "$(rbenv init -)"
cd $ITPKG_HOME/current 
rake db:migrate 
rake db:seed 
rake assets:precompile 
EOF

systemctl restart itpkg-bg
systemctl restart itpkg-wss
systemctl restart itpkg-www
