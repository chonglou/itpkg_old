class Task < ActiveRecord::Base
  include FakeDestroy

  belongs_to :story
  has_many :task_comments, dependent: :destroy

  after_save :update_story

  validates_presence_of :point, :status, :priority

  enum status: {submit: 0, processing: 1, finish: 2, reject: 3, done: 9}
  enum priority: {immediately: -10, high: -1, normal: 0, low: 1, ignore: 10}

  def to_html
    Itpkg::StringHelper.md2html self.details
  end

  private
  def update_story
    task_points_sum = self.story.tasks.sum(:point)
    story_status    = self.story.tasks.minimum(:status)
    self.story.update(point: task_points_sum, status: story_status)
  end
end
