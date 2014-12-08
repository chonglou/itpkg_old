#!/bin/sh


su deploy <<'EOF'
mkdir -p $ITPKG_HOME/shared/.ssh
chmod 700 $ITPKG_HOME/shared/.ssh
ssh-keygen -b 2048 -t rsa  -f $ITPKG_HOME/shared/.ssh/id_rsa -q -N ""
cp $ITPKG_HOME/shared/.ssh/id_rsa.pub /tmp
EOF

su git <<'EOF'
~/local/gitolite/gitolite setup -pk /tmp/id_rsa.pub 
EOF

rm /tmp/id_rsa.pub
