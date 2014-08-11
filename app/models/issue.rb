class Issue < ActiveRecord::Base
  has_one :user, class_name: 'BrahmaBodhi::User'
  has_many :replies
  belongs_to :project
  enum flag: {feature: 0, bug: 1}
  enum state: {submit: 0, open: 1, ingnore: 2, close:3, finish:99}
end
