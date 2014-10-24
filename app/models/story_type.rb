class StoryType < ActiveRecord::Base
  belongs_to :story
  belongs_to :s_type
end
