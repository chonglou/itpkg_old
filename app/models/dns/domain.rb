class Dns::Domain < ActiveRecord::Base
  belongs_to :host
  has_many :records
end
