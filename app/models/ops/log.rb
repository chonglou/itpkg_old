class Ops::Log < ActiveRecord::Base
  belongs_to :node
  has_one :user, class_name: 'BrahmaBodhi::User'
end
