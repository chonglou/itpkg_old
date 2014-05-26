class Dns::Host < ActiveRecord::Base
  has_one :client
  has_many :domains
end
