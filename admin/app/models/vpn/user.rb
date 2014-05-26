class Vpn::User < ActiveRecord::Base
  belongs_to :host
end
