class Vpn::Host < ActiveRecord::Base
  has_many :logs
  has_one :certificate
  attr_encrypted :password, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt

  validates :name, :domain, :ip, :network, :routes, :dns, presence: true
  validates :ip, :domain, uniqueness: true
end
