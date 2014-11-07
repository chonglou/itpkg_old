class Cdn::Nginx < ActiveRecord::Base
  has_many :memcacheds, through: 'NginxMemcached'
  has_many :servers, through: 'NginxServer'
  has_one :certificate
end
