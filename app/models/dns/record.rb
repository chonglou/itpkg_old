class Dns::Record < ActiveRecord::Base
  validates :flag, :host, :ttl, :zone, :code, presence: true
  validates :host, uniqueness: {scope: [:flag, :zone, :code]}
end
