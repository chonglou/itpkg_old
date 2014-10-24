class CreateContacts < ActiveRecord::Migration
  def change
    create_table :contacts do |t|
      t.integer :user_id, null:false
      t.string :logo, null:false
      t.string :username, null:false
      t.text :content, null:false
      t.timestamps
    end

    add_index :contacts, :username
  end
end
