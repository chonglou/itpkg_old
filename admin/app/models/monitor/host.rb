class Monitor::Host < ActiveRecord::Base
  has_one :client
  belongs_to :user, class_name:'BrahmaBodhi::User'
end
