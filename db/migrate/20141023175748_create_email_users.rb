class CreateEmailUsers < ActiveRecord::Migration
  def change
    create_table :email_users do |t|
      t.integer :domain_id, null:false
      t.string :passwd, null:false
      t.string :email, null:false
      t.timestamps
    end
    add_index :email_users, :email, unique: true
  end
end
