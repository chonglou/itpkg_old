class Contact < ActiveRecord::Base
  belongs_to :user

  validates :user_id, :logo, :username, :content, presence: true

end
