# This migration comes from brahma_bodhi (originally 20140427025416)
class CreateBrahmaBodhiPermissions < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_permissions do |t|
      t.string :resource, null: false
      t.string :role, null: false
      t.string :operation, null: false
      t.datetime :startup, null: false
      t.datetime :shutdown, null: false
      t.datetime :created, null: false
      t.integer :version, null: false, default: 0
    end
    add_index :brahma_bodhi_permissions, :role
    add_index :brahma_bodhi_permissions, :resource
    add_index :brahma_bodhi_permissions, :operation

  end
end
