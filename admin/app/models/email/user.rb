class Email::User < ActiveRecord::Base
  belongs_to :domain
end
