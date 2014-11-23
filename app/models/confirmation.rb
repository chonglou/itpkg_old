class Confirmation < ActiveRecord::Base
  belongs_to :user
  attr_encrypted :extra, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt

  validates :token, :extra, :user_id, :deadline, :status, :subject, presence: true
  validates :token, uniqueness: true
  enum status: {submit: 0, done: -1}
end
