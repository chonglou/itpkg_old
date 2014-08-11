class ProjectUser < ActiveRecord::Base
  belongs_to :project
  has_one :member, class_name: 'BrahmaBodhi::User'
end
