class Contact < ActiveRecord::Base
  belongs_to :user

  validates :user_id, :logo, :username, :label, :content, presence: true
  validates :label, uniqueness:true
end
