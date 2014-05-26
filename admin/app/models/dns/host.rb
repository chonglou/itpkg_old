class Dns::Host < ActiveRecord::Base
  belongs_to :user, class_name: 'BrahmaBodhi::User'
  has_one :client
  has_many :domains
end
