# This migration comes from brahma_bodhi (originally 20140427025321)
class CreateBrahmaBodhiSettings < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_settings do |t|
      t.string :key, null: false
      t.binary :val, null: false
      t.datetime :created, null: false
      t.integer :version, null: false, default: 0
    end
    add_index :brahma_bodhi_settings, :key, unique: true
  end
end
