class CreateClients < ActiveRecord::Migration
  def change
    create_table :clients do |t|
      t.integer :user_id, null: false
      t.string :name, null: false
      t.text :details
      t.integer :flag, null: false, limit: 2, default: 0
      t.integer :state, null: false, limit: 2, default: 0
      t.string :serial, null: false
      t.string :secret, null: false
      t.datetime :created, null: false
    end
    add_index :clients, :serial, unique: true
  end
end
