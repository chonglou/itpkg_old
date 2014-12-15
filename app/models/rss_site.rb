class RssSite < ActiveRecord::Base
  validates :url, presence: true
  validates :url, uniqueness: true
end
