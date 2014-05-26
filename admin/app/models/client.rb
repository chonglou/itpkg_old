class Client < ActiveRecord::Base
  has_one :user, class_name:'BrahmaBodhi::User'
end
