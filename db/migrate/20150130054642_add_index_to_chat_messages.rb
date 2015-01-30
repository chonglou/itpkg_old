class AddIndexToChatMessages < ActiveRecord::Migration
  def change
    add_index :chat_messages, :node
    add_index :chat_messages, :domain
    add_index :chat_messages, :resource
  end
end
