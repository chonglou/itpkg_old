class Story < ActiveRecord::Base
  belongs_to :project
  has_many :s_tasks
  has_many :s_types, through: 'StoryType'
  has_many :s_tags, through: 'StoryTag'
end
