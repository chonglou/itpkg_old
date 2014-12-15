class LoggingNode < ActiveRecord::Base

  validates :vip, :flag, :name, presence: true
  validates :vip, uniqueness: true

  enum flag: {submit:0,journald:1, rsyslog:2}
end
