class CreateSshKeys < ActiveRecord::Migration
  def change
    create_table :ssh_keys do |t|
      t.integer :user_id, null:false
      t.text :public_key, null:false
      t.text :encrypted_private_key, null: false
      t.string :encrypted_private_key_salt, null: false
      t.string :encrypted_private_key_iv, null: false

      t.timestamps
    end
    add_index :ssh_keys, :user_id, unique:true
  end
end
