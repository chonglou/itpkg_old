class Project < ActiveRecord::Base
  validates :name, presence: true

  has_many :stories
  has_many :story_tags,  through: 'Story'
  has_many :story_types, through: 'Story'
  has_many :tasks,       through: 'Story'
end
