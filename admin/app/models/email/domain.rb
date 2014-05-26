class Email::Domain < ActiveRecord::Base
  belongs_to :host
  has_many :users
end
