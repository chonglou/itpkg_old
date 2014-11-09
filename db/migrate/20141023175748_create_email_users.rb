class CreateEmailUsers < ActiveRecord::Migration
  def change
    create_table :email_users do |t|
      t.integer :domain_id, null: false
      t.string :passwd, null: false
      t.string :name, null: false, limit:32
      t.timestamps
    end
    add_index :email_users, :name
  end
end
