class Monitor::Group < ActiveRecord::Base
  has_many :nodes
end
