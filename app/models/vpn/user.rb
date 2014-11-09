class Vpn::User < ActiveRecord::Base
  alias_attribute :password, :passwd

  validates :email, :start_date, :end_date, presence: true
  validates :email, uniqueness: true
  validates :password, presence: true, length: {minimum: 6}

end
