class CreateEmailUsers < ActiveRecord::Migration
  def change
    create_table :email_users do |t|
      t.integer :domain_id, null: false
      t.string :password, null: false
      t.string :email, null: false, limit:32
      t.timestamps
    end
    add_index :email_users, :email, unique:true
  end
end
