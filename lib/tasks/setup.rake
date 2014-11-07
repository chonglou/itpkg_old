require 'json'
require 'highline/import'
require_relative '../itpkg/utils/encryptor'

namespace :itpkg do
  desc 'Shell env'
  task :env do
    # types = %w(local s3)
    # type = ask("Attachment storage type? (#{types.join '/'})") { |q| q.default=types[0] }.to_s
    # case type
    #   when 's3'
    #     fog={
    #         provider: 'AWS',
    #         aws_access_key_id: ask('Access Key Id').to_s,
    #         aws_secret_access_key: ask('Secret Access Key').to_s,
    #         region: ask('Region').to_s,
    #         host: ask('Host').to_s,
    #         endpoint: ask('Endpoint').to_s
    #     }
    #   else
    #     fog={
    #         provider: 'Local',
    #         local_root: ask('Local Root: ').to_s,
    #         endpoint: ask('Endpoint: ').to_s
    #     }
    # end

    # ITPKG_ATTACHMENT_CREDENTIALS='#{fog.to_json}'
    # ITPKG_ATTACHMENT_BUCKET="#{ask('Bucket ?') { |q| q.default='uploads' }}"
    # ITPKG_ATTACHMENT_CREDENTIALS ITPKG_ATTACHMENT_BUCKET


    puts <<-EOF
################# BEGIN #################
RAILS_ENV=production
ITPKG_DATABASE_PASSWORD="#{ask('Mysql Root Password? ') { |q| q.default='' }}"
ITPKG_DOMAIN="#{ask('Domain? ') { |q| q.default='localhost' }}"
ITPKG_MEMCACHED_HOSTS="#{ask('Memcached Hosts(split by \',\')? ') { |q| q.default='localhost' }}"
ITPKG_MAILER_SENDER="#{ask('Mail Sender Name? ') { |q| q.default='admin' }}"
ITPKG_SECRET_KEY_BASE="#{`pwgen -n 128`.strip}"
ITPKG_DEVISE_SECRET_KEY="#{`pwgen -n 128`.strip}"
ITPKG_PASSWORD="#{`pwgen -n 128`.strip}"
ITPKG_CIPHER="#{Itpkg::Encryptor.generate}"
ITPKG_REDIS_URL=#{ask('Redis Provider? '){|q|q.default='redis://localhost:6379/0'}}

export RAILS_ENV ITPKG_DATABASE_PASSWORD ITPKG_DOMAIN ITPKG_MEMCACHED_HOSTS ITPKG_MAILER_SENDER ITPKG_SECRET_KEY_BASE ITPKG_DEVISE_SECRET_KEY ITPKG_PASSWORD ITPKG_CIPHER ITPKG_REDIS_URL
################# END ###################
Please parse to your shell config file.
    EOF
  end
end