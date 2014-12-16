class LoggingSearch < ActiveRecord::Base
  validates :name, :keyword, :tag, :vip, :hostname, presence: true
end
