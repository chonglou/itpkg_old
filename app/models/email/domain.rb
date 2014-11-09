class Email::Domain < ActiveRecord::Base
  has_many :users, class_name: 'Email::User'
  has_many :aliases, class_name: 'Email::Alias'

  validates :name, presence: true
  validates :name, uniqueness: true

end
