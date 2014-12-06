

class Project < ActiveRecord::Base
  validates :name, :creator_id, presence: true

  has_one :creator, class_name: 'User'
  has_many :users, through: 'ProjectUser'
  has_many :stories
  has_many :s_types
  has_many :s_tags

end
