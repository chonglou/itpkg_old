cd /etc/ejabberd && sed -i '/hosts/{n;s/.*/  - "localhost.localdomain"/}' ejabberd.yml
systemctl restart ejabberd
