class Cdn::NginxServer < ActiveRecord::Base
  belongs_to :nginx
  belongs_to :server
end
