class Client < ActiveRecord::Base
  has_one :user, class_name: 'BrahmaBodhi::User'
  enum flag: {unknown:0, firewall: 1, cdn: 2, vpn: 3, dns: 4, email: 5, monitor: 6}
  enum state: {submit: 0, enable: 1, disable: 2}
end
