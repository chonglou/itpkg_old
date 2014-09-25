class Team::ProjectMember < ActiveRecord::Base
  belongs_to :project
  belongs_to :member, class_name: 'BrahmaBodhi::User'
end
