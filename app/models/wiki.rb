class Wiki < ActiveRecord::Base
  resourcify
  paginates_per 5

  validates :title, :body, presence: true

  enum status: {personal: 1, publish: 0}
end
