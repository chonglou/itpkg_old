

class Project < ActiveRecord::Base
  validates :name, :creator_id, presence: true

  belongs_to :creator,      class_name: 'User'

  has_and_belongs_to_many :users, through: :project_users

  has_many :stories
  has_many :story_tags,  through: 'Story'
  has_many :story_types, through: 'Story'
  has_many :tasks,       through: 'Story'

end
