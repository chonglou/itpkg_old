class Wiki < ActiveRecord::Base
  belongs_to :project

  validates :title, :body, presence: true

  enum status: {project: 0, personal: 1, publish: 2}
end
