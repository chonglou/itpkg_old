class CreateOpsLogs < ActiveRecord::Migration
  def change
    create_table :ops_logs do |t|
      t.integer :node_id, null:false
      t.integer :user_id, null:false
      t.string :message, null:false
      t.timestamps
    end
  end
end
