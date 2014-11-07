class Cdn::NginxMemcached < ActiveRecord::Base
  belongs_to :nginx
  belongs_to :memcached
end
