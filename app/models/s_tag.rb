class STag < ActiveRecord::Base
  belongs_to :project
  has_many :stories, through: 'StoryTag'
end
