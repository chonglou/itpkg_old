class LoggingNodeUser < ActiveRecord::Base
  belongs_to :logging_node
  belongs_to :user
end
