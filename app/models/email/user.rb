class Email::User < ActiveRecord::Base
  belongs_to :domain
  enum state:{submit:0, enable:1, disable:2}
end
