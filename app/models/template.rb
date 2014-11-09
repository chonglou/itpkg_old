class Template < ActiveRecord::Base

  validates :flag, :name, :body, :owner, presence: true
  validates :name, uniqueness: {scope: :flag}
end
