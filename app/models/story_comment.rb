class StoryComment < ActiveRecord::Base
  include FakeDestroy

  belongs_to :user
  belongs_to :story

  validates_presence_of :content, :user_id, :story_id
end
