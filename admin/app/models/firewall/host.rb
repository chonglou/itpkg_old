class Firewall::Host < ActiveRecord::Base
  has_one :client
  has_many :devices
  has_many :domains
  has_many :limits
  has_many :inputs
  has_many :outputs
  has_many :nats

end
