class MonitorNode < ActiveRecord::Base

  validates :uuid, :flag, :name, :cfg, presence: true
  validates :uid, uniqueness: true

  attr_encrypted :cfg, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt

  enum flag: {ping:0, snmp:1, smtp:11, pop3:12, imap:13, nginx: 21, http:22}
end
