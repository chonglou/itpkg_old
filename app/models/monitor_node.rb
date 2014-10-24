class MonitorNode < ActiveRecord::Base
  has_one :creator, class_name: 'User'
  has_many :users, through: 'MonitorNode'
  enum status: {submit: 0, running: 1, stop: 1}
  enum flag: {snmp: 0, ping: 1, smtp: 20, mysql: 50, http: 70, nginx: 71}
end
