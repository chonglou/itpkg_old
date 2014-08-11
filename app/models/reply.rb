class Reply < ActiveRecord::Base
  belongs_to :issue
  has_one :user, class_name: 'BrahmaBodhi::User'
end
