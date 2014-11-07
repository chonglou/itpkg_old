class RepositoryUser < ActiveRecord::Base
  belongs_to :user
  belongs_to :repository
  has_one :certificate
end
