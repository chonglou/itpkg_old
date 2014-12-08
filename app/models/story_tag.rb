class StoryTag < ActiveRecord::Base
  belongs_to :project
  has_and_belongs_to_many :stories
end
