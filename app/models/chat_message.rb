class ChatMessage < ActiveRecord::Base
  enum flags: {chat:1, groupchat:2, headline:3, normal:4, error:99}
end
