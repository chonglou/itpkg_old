class MonitorNode < ActiveRecord::Base

  validates :vip, :flag, :name, :cfg, presence: true
  validates :vip, uniqueness: true

  attr_encrypted :cfg, key: ENV['ITPKG_PASSWORD'], mode: :per_attribute_iv_and_salt

  enum flag: {
           unknown:0,
           ping:1, snmp:2,
           smtp:11, pop3:12, imap:13,
           http:20, nginx: 21, memcached:51,
           redis:52,
           mysql:61
       }
end
