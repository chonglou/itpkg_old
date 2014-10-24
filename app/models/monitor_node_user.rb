class MonitorNodeUser < ActiveRecord::Base
  belongs_to :monitor_node
  belongs_to :user
end
