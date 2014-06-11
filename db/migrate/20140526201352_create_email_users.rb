class CreateEmailUsers < ActiveRecord::Migration
  def change
    create_table :email_users do |t|
      t.integer :domain_id, null:false
      t.string :username, null: false
      t.string :password, null: false
      t.integer :state, null: false, limit: 2, default: 0
      t.datetime :created, null: false
    end
  end
end
