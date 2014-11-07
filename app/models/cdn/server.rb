class Cdn::Server < ActiveRecord::Base
  has_many :nginxes, through: 'NginxServer'
end
