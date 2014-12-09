

class Project < ActiveRecord::Base
  validates :name, :creator_id, presence: true

  belongs_to :creator,      class_name: 'User'

  has_many :project_users
  has_many :users, through: :project_users

  has_many :stories
  has_many :story_tags,  through: 'Story'
  has_many :story_types, through: 'Story'
  has_many :tasks,       through: 'Story'

end
