require 'highline/import'

namespace :itpkg do
  desc 'Shell env'
  task :env do
    puts <<-EOF
################# BEGIN #################
ITPKG_DATABASE_PASSWORD="#{ask('Mysql Root Password? '){|q|q.default=''}}"
ITPKG_DOMAIN="#{ask('Domain? '){|q| q.default='localhost'}}"
ITPKG_MEMCACHED_HOSTS="#{ask('Memcached Hosts(split by \',\')? '){|q| q.default='localhost'}}"
ITPKG_MAILER_SENDER="#{ask('Mail Sender Name? '){|q|q.default='admin'}}"
ITPKG_SECRET_KEY_BASE="#{`pwgen -n 128`.strip}"
ITPKG_DEVISE_SECRET_KEY="#{`pwgen -n 128`.strip}"
export ITPKG_DATABASE_PASSWORD ITPKG_DOMAIN ITPKG_MEMCACHED_HOSTS ITPKG_MAILER_SENDER ITPKG_SECRET_KEY_BASE ITPKG_DEVISE_SECRET_KEY
################# END ###################
Please parse to your shell config file.
EOF
  end
end