class CreateConfirmations < ActiveRecord::Migration
  def change
    create_table :confirmations do |t|
      t.string :subject, null:false
      t.integer :status, null:false, default:0
      t.string :token, null:false, limit:36
      t.text :encrypted_extra, null: false
      t.string :encrypted_extra_salt, null: false
      t.string :encrypted_extra_iv, null: false
      t.integer :user_id, null:false, default:0
      t.datetime :deadline, null:false
      t.timestamps
    end
    add_index :confirmations, :token, unique:true
  end
end
