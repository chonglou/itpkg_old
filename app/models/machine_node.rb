class MachineNode < ActiveRecord::Base
  has_one :creator, class_name: 'User'
  has_many :users, through: 'MachineNode'
  enum status: {submit: 0, running: 1, stop: 1}
end
