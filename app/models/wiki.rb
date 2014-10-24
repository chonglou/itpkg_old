class Wiki < ActiveRecord::Base
  belongs_to :project
  belongs_to :creator, class_name: 'User'
  belongs_to :author, class_name: 'User'
  has_many :users

  enum status: {project:0, personal:1, publish:2}
end
