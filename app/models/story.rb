class Story < ActiveRecord::Base
  belongs_to :project
  has_many :s_tasks
  has_many :s_types, through: 'StoryType'
  has_many :s_tags, through: 'StoryTag'

  enum status: {submit: 0, processing: 1, finish: 2, reject: 3, done: 9}
end
