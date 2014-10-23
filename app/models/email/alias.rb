class Email::Alias < ActiveRecord::Base
  belongs_to :domain, class_name: 'Email::Domain'
end
