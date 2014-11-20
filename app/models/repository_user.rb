class RepositoryUser < ActiveRecord::Base
  belongs_to :user
  belongs_to :repository

  validates :user_id, :repository_id, presence: true
  validates :user_id, uniqueness: {scope: :repository_id}
end
