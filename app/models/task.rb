class Task < ActiveRecord::Base
  include FakeDestroy

  belongs_to :story
  has_many :task_comments, dependent: :destroy

  enum status: {at_once: -10, high: -1, normal: 0, low: 1, ingnore: 10}
end
