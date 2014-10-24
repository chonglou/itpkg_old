class Finance::Score < ActiveRecord::Base
  belongs_to :project
  belongs_to :tag
  has_one :user

end
