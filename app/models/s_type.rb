class SType < ActiveRecord::Base
  belongs_to :project
  has_many :stories, through: 'StoryType'
end
