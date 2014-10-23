require 'json'
require 'carrierwave'
require 'carrierwave/orm/activerecord'

CarrierWave::SanitizedFile.sanitize_regexp = /[^[:word:]\.\-\+]/

CarrierWave.configure do |config|
  config.permissions = 0600
  config.directory_permissions = 0700
  begin
    config.fog_credentials = Hash[JSON.parse(ENV['ITPKG_ATTACHMENT_CREDENTIALS']).map { |k, v| [k.to_sym, v] }]
    config.fog_directory = ENV['ITPKG_ATTACHMENT_BUCKET']
    config.fog_public = true
    config.fog_attributes = {'Cache-Control' => "max-age=#{365.day.to_i}"}
  rescue TypeError
    Rails.logger.error 'error on setting fog.'
  end
end