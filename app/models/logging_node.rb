class LoggingNode < ActiveRecord::Base

  validates :vip, :flag, :name, presence: true
  validates :vip, uniqueness: true

  enum flag: {enable:1, disable:2}
end
