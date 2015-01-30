class CreateChatMessages < ActiveRecord::Migration
  def change
    create_table :chat_messages do |t|
      t.string :from, null:false
      t.string :to, null:false
      t.string :body, null:false, limit:1024
      t.integer :flag, null:false, default:0
      t.timestamp :created, null: false
    end
    add_index :chat_messages, :from
    add_index :chat_messages, :to
  end
end
