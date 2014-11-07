class Cdn::Memcached < ActiveRecord::Base
  has_many :nginxes, through: 'NginxMemcached'
end
