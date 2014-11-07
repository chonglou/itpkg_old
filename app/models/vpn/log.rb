class Vpn::Log < ActiveRecord::Base
  has_one :host
end
