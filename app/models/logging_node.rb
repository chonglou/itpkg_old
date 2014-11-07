class LoggingNode < ActiveRecord::Base
  has_one :creator, class_name: 'User'
  has_many :users, through: 'LoggingNode'
  enum status: {submit: 0, running: 1, stop: 1}
  enum flag: {normal: 0}
  has_one :certificate
end
