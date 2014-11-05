class Vpn::User < ActiveRecord::Base
  validates :name, :email, :start_date, :end_date, presence:true
end
