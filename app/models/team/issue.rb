class Team::Issue < ActiveRecord::Base
  belongs_to :project
  has_many :activities
  has_one :worker, class_name: 'BrahmaBodhi::User'
  has_one :creator, class_name: 'BrahmaBodhi::User'
end
