class Repository < ActiveRecord::Base
  has_many :logs
  has_many :users, through: 'RepositoryUser'
  belongs_to :creator, class_name: 'User'

  validates :creator_id, :name, :title, presence: true
  validates :name, uniqueness:true
  validates_format_of :name, with: /\A[a-zA-Z][a-zA-Z0-9_]{2,12}\z/, on: :create
end
