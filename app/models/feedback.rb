class Feedback < ActiveRecord::Base
  include FakeDestroy

  belongs_to :project
  belongs_to :user

  validates_presence_of :name, :email, :content

  enum status: {submit: 0, processing: 1, done: 2}

end
