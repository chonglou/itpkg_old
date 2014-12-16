require 'elasticsearch/persistence/model'

class LoggingItem
  include Elasticsearch::Persistence::Model

  attribute :vip, String
  attribute :vport, Integer
  attribute :hostname, String
  attribute :tag, String
  attribute :pid, Integer
  attribute :message, String
  attribute :created, Time

  validates :vip, :vport, :hostname, :created, :message, presence: true

  def to_s
    "#{self.created} #{self.hostname} #{self.tag}[#{self.pid}]: #{self.message}"
  end
end