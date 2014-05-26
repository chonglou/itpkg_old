class Router::Firewall::Output < ActiveRecord::Base
  belongs_to :host
  has_many :output_devices
  has_many :devices, through: :output_devices
end
