class StoryTag < ActiveRecord::Base
  belongs_to :story
  belongs_to :s_tag
end
