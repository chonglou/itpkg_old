namespace :setup do

  desc 'Shell env'
  task :env do
    require 'json'
    require 'highline/import'
    require_relative '../itpkg/utils/encryptor'

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

    File.open("#{Rails.root}/.rbenv-vars", 'w') do |f|
      f.write <<-EOF
RAILS_ENV=production
ITPKG_DATABASE_PASSWORD=#{ask('Mysql Root Password? ') { |q| q.default='' }}
ITPKG_DOMAIN=#{ask('Domain? ') { |q| q.default='localhost' }}
# split by ';'
ITPKG_MEMCACHED_HOSTS=#{ask('Memcached Hosts(split by \',\')? ') { |q| q.default='localhost' }}
#ITPKG_MAILER_SENDER=#{ask('Mail Sender Name? ') { |q| q.default='no-reply' }}@$ITPKG_DOMAIN
ITPKG_REDIS_URL=#{ask('Redis Provider? ') { |q| q.default='redis://localhost:6379/0' }}
# can be generate by 'pwgen -n 128'
ITPKG_SECRET_KEY_BASE=#{`pwgen -n 128`.strip}
ITPKG_DEVISE_SECRET_KEY=#{`pwgen -n 128`.strip}
ITPKG_PASSWORD=#{`pwgen -n 128`.strip}
      EOF
    end
  end
end