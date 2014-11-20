class Repository < ActiveRecord::Base
  has_many :logs
  has_many :users, through: 'RepositoryUser'

  validates :creator_id, :name, :title, presence: true

  validates :name, uniqueness:true
end
