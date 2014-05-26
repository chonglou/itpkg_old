class Monitor::Host < ActiveRecord::Base
  has_one :client
end
