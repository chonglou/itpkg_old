#!/bin/sh

password=$(pwgen 16) 

mysql -u root -h localhost -e "CREATE DATABASE itpkg CHARACTER SET utf8;GRANT ALL PRIVILEGES ON itpkg.* TO 'itpkg'@'localhost' IDENTIFIED BY '$password';FLUSH PRIVILEGES;" 

su deploy<<'EOF'
sed -i "s/^ITPKG_DATABAE_PASSWORD=.*/ITPKG_DATABASE_PASSWORD=$password/g" $ITPKG_HOME/shared/.rbenv-vars
export PATH="$HOME/.rbenv/bin:$PATH"
eval "$(rbenv init -)"
cd $ITPKG_HOME/current 
rake db:migrate 
rake db:seed 
rake assets:precompile 
EOF
