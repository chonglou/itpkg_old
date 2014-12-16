namespace :setup do
  # desc "'Update domain to #{ENV['ITPKG_DOMAIN']}'"
  # task domain: :environment do
  #   User.all.each do |u|
  #     o_e = u.email
  #     n_e = "#{u.label}@#{ENV['ITPKG_DOMAIN']}"
  #     u.update(email:n_e)
  #     puts "#{o_e} => #{n_e}"
  #   end
  # end

#   desc 'Shell env'
#   task :env do
#     require 'json'
#     require 'highline/import'
#     require_relative '../itpkg/utils/encryptor'
#
#
#     File.open("#{Rails.root}/.rbenv-vars", 'w') do |f|
#       f.write <<-EOF
# RAILS_ENV=production
# ITPKG_DATABASE_PASSWORD=#{ask('Mysql Root Password? ') { |q| q.default='' }}
# ITPKG_DOMAIN=#{ask('Domain? ') { |q| q.default='localhost.localdomain' }}
# # split by ';'
# ITPKG_MEMCACHED_HOSTS=#{ask('Memcached Hosts(split by \',\')? ') { |q| q.default='localhost' }}
# #ITPKG_MAILER_SENDER=#{ask('Mail Sender Name? ') { |q| q.default='no-reply' }}@$ITPKG_DOMAIN
# ITPKG_REDIS_URL=#{ask('Redis Provider? ') { |q| q.default='redis://localhost:6379/0' }}
# # can be generate by 'pwgen -n 128'
# ITPKG_SECRET_KEY_BASE=#{`pwgen -n 128`.strip}
# ITPKG_DEVISE_SECRET_KEY=#{`pwgen -n 128`.strip}
# ITPKG_PASSWORD=#{`pwgen -n 128`.strip}
#       EOF
#     end
#  end
end