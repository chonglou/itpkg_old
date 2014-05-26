class CreateClients < ActiveRecord::Migration
  def change
    create_table :clients do |t|
      t.string :serial, null: false
      t.string :secret, null: false
      t.datetime :created, null:false
    end
     add_index :clients, :serial, unique: true
  end
end
