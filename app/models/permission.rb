class Permission < ActiveRecord::Base
  validates :role, :resource, :operation, :start_date, :end_date, presence: true
  validates :role, uniqueness: {scope: [:resource, :operation]}
end
