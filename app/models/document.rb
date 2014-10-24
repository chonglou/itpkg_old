class Document < ActiveRecord::Base
  belongs_to :project
  belongs_to :creator, class_name: 'User'
  enum status: {project: 0, personal: 1, publish: 2}
end
