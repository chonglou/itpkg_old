class Vpn::Log < ActiveRecord::Base
  validates :user, presence: true
end
