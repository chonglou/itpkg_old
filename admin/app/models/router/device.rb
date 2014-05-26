class Router::Device < ActiveRecord::Base
  belongs_to :host
  belongs_to :limit
  has_many :output_devices
  has_many :outputs, through: :output_devices
  enum state: {submit:0, disable:1, enable:2}
end
