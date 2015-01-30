class AddChatPasswordToUsers < ActiveRecord::Migration
  def change
    change_table :users do |t|
      t.string :encrypted_chat_password
      t.string :encrypted_chat_password_salt
      t.string :encrypted_chat_password_iv
    end
  end
end
