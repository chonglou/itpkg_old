class MailBoxDocument < ActiveRecord::Base
  belongs_to :mail_box
  belongs_to :document
end
