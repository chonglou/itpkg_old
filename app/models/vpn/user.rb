

class Vpn::User < ActiveRecord::Base
  validates :email, :start_date, :end_date, presence:true
  validates :email, uniqueness: true
  validates :passwd, presence: true, length: {minimum: 6}#, confirmation: true
  #validates_confirmation_of :passwd

end
