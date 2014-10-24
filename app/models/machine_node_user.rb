class MachineNodeUser < ActiveRecord::Base
  belongs_to :machine_node
  belongs_to :user
end
