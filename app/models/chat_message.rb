class ChatMessage < ActiveRecord::Base
  enum flags: {chat:1, error:99}
end
