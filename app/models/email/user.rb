class Email::User < ActiveRecord::Base
  belongs_to :domain, class_name: 'Email::Domain'

  validates :email, presence: true, uniqueness: true
  validates :password, presence: true, length: {minimum: 6}
end
