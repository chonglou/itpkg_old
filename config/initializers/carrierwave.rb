require 'carrierwave'
#require 'carrierwave/orm/activerecord'
require 'carrierwave/mongoid'

CarrierWave::SanitizedFile.sanitize_regexp = /[^[:word:]\.\-\+]/

CarrierWave.configure do |config|
  config.storage = :grid_fs
  config.root = Rails.root.join('tmp')
  config.cache_dir = 'uploads'
  #todo
  config.grid_fs_access_url = '/attachments'

  # config.storage = :fog
  # config.permissions = 0600
  # config.directory_permissions = 0700
  #
  # config.fog_credentials = {
  #     provider: 'Local',
  #     local_root: "#{Rails.root}/tmp/storage",
  #     endpoint: Rails.env.production? ? "https://www.#{ENV['ITPKG_DOMAIN']}/attachments" : 'http://localhost:3000/attachments'
  # }
  # config.fog_directory = 'uploads'
  # config.fog_public = true
  # config.fog_attributes = {'Cache-Control' => "max-age=#{365.day.to_i}"}

end