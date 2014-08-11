class Project < ActiveRecord::Base
  has_one :owner, class_name: 'BrahmaBodhi::User'
  has_many :members, through: :project_users
  has_many :issues
end
