class ChatMessage < ActiveRecord::Base
  enum flags: {chat: 1, groupchat: 2, headline: 3, normal: 4, error: 99}
  validates :to, :flag, :created, :body, presence: true

  def from
    s = self.domain
    s = "#{self.node}@#{s}" if self.node
    s
  end
end
