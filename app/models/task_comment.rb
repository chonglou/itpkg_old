class TaskComment < ActiveRecord::Base
  belongs_to :user
  belongs_to :task

  validates_presence_of :content, :user_id, :task_id
end
