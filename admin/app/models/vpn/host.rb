class Vpn::Host < ActiveRecord::Base
  has_one :client
  has_many :users
end
