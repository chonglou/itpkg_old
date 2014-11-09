class Email::Host < ActiveRecord::Base
  attr_encrypted :password, key:ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt
  has_one :certificate

   validates :name, :ip, presence: true
  validates :ip, uniqueness: true

end
