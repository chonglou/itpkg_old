class LoggingNode < ActiveRecord::Base
  paginates_per 50
  validates :vip, :flag, :name, presence: true
  validates :vip, uniqueness: true

  enum flag: {submit:0, enable:1, disable:2}
  def to_s
    "#{self.name}<#{self.vip}>"
  end
end
