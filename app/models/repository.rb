class Repository < ActiveRecord::Base
  has_many :logs
  has_many :users, through: 'RepositoryUser'
end
