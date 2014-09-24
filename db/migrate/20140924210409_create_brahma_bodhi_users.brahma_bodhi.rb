# This migration comes from brahma_bodhi (originally 20140427030709)
class CreateBrahmaBodhiUsers < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_users do |t|
      t.string :open_id, null: false
      t.string :username, null:false
      t.integer :flag, null: false, default: 0, limit: 1
      t.integer :state, null: false, default: 0, limit: 1
      t.binary :contact, null:false
      t.datetime :last_login
      t.datetime :created, null: false
    end
    add_index :brahma_bodhi_users, :open_id, unique: true
  end
end
