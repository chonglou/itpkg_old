class Cdn::Nginx < ActiveRecord::Base
  has_many :memcacheds, through: 'NginxMemcached'
  has_many :servers, through: 'NginxServer'
  attr_encrypted :key, key:ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt
end
