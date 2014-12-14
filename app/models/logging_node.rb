class LoggingNode < ActiveRecord::Base

  validates :uuid, :flag, :name, :cfg, presence: true
  validates :uid, uniqueness: true

  attr_encrypted :cfg, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt

  enum flag: {journald:0, rsyslog:1}
end
