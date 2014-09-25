class Team::Project < ActiveRecord::Base
  has_many :documents
  has_many :issues
  has_many :members, through: :project_member, class_name: 'BrahmaBodhi::User'
  has_one :manager, class_name: 'BrahmaBodhi::User'
end
