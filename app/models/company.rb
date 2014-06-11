class Company < ActiveRecord::Base
  belongs_to :user, class_name: 'BrahmaBodhi::User'
end
