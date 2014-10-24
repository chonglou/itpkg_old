class Finance::Tag < ActiveRecord::Base
  has_many :scores
  belongs_to :project
end
