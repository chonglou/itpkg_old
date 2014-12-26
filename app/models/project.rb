class Project < ActiveRecord::Base
  include FakeDestroy
  resourcify

  validates :name, presence: true

  has_many :stories,     dependent: :destroy
  has_many :story_tags,  through: 'Story'
  has_many :story_types, through: 'Story'
  has_many :tasks,       through: 'Story'
end
