#!/bin/sh

password=$(pwgen 16)
htpasswd -b -c /etc/nginx/itpkg/.htpasswd deploy $password
chmod 600 /etc/nginx/itpkg/.htpasswd


su deploy <<'EOF'
sed -i "s/^ITPKG_DOCKER_PASSWORD=.*/ITPKG_DOCKER_PASSWORD=$password/g" $ITPKG_HOME/shared/.rbenv-vars
mkdir -p $ITPKG_HOME/shared/.ssh
chmod 700 $ITPKG_HOME/shared/.ssh
ssh-keygen -b 2048 -t rsa  -f $ITPKG_HOME/shared/.ssh/id_rsa -q -N ""
cp $ITPKG_HOME/shared/.ssh/id_rsa.pub /tmp
EOF

su git <<'EOF'
~/local/gitolite/gitolite setup -pk /tmp/id_rsa.pub 
EOF

rm /tmp/id_rsa.pub
