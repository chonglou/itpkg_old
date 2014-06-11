class Firewall::Limit < ActiveRecord::Base
  belongs_to :host
  has_many :devices
end
