class Confirmation < ActiveRecord::Base
  belongs_to :user
  attr_encrypted :extra, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt

  validates :token, :extra, :user_id, :deadline, presence: true
  validates :token, uniqueness: true
end
