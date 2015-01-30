class AddNodeResourceToChatMessages < ActiveRecord::Migration
  def change
    remove_index :chat_messages, :from
    remove_column :chat_messages, :from

    change_table :chat_messages do |t|
      t.string :node
      t.string :domain
      t.string :resource
    end
  end
end
