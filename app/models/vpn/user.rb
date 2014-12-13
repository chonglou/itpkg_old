class Vpn::User < ActiveRecord::Base
  validates :name, :start_date, :end_date, presence: true
  validates :name, uniqueness: true
  validates :password, presence: true, length: {minimum: 6}

end
