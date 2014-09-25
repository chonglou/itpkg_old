class Ops::Node < ActiveRecord::Base
  has_many :logs
  belongs_to :group
end
