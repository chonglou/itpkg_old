#!/bin/sh


su deploy <<'EOF'
mkdir -p $ITPKG_HOME/shared/.ssh
chmod 700 $ITPKG_HOME/shared/.ssh
ssh-keygen -b 2048 -t rsa  -f $ITPKG_HOME/shared/.ssh/deploy -q -N ""
cp $ITPKG_HOME/shared/.ssh/deploy.pub /tmp
EOF

chown -R git:git ~git/repositories
su git <<'EOF'
~/local/gitolite/gitolite setup -pk /tmp/deploy.pub 
EOF

rm /tmp/deploy.pub
