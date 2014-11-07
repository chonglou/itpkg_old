class Vpn::Host < ActiveRecord::Base
  has_many :logs
  attr_encrypted :password, key:ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt
end
