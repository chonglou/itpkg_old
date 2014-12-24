class Log < ActiveRecord::Base
  belongs_to :user
  before_create :set_created

  def set_created
    self.created = Time.now
  end
end
