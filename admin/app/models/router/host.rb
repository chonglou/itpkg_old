class Router::Host < ActiveRecord::Base
  belongs_to :user, class_name: 'BrahmaBodhi::User'
  has_one :client
  has_many :devices
  has_many :domains
  has_many :limits
  has_many :inputs
  has_many :outputs
  has_many :nats

end
