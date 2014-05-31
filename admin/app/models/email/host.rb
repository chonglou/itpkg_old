class Email::Host < ActiveRecord::Base
  belongs_to :client
  has_many :domains
end
