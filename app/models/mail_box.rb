class MailBox < ActiveRecord::Base
  has_one :user
  has_many :documents
  enum status: {in_box: 0, out_box: 1, draft: 3, trash: 6, spam: 9}
end
