class SshKey < ActiveRecord::Base
  validates :user_id, :public_key, :private_key, presence: true
  validates :user_id, uniqueness:true
  attr_encrypted :private_key, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt
  belongs_to :user
end
