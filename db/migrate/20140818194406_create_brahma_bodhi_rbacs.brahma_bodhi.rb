# This migration comes from brahma_bodhi (originally 20140818193917)
class CreateBrahmaBodhiRbacs < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_rbacs do |t|
      t.string :resource, null:false
      t.string :operation, null:false
      t.string :role, null:false
      t.datetime :startup, null:false, default:'9999-12-31 23:59:59'
      t.datetime :shutdown, null:false, default:'1000-01-01 00:00:00'
      t.timestamps
    end

    add_index :brahma_bodhi_rbacs, :resource
    add_index :brahma_bodhi_rbacs, :operation
    add_index :brahma_bodhi_rbacs, :role
  end
end
