class CreateMonitorNodeUsers < ActiveRecord::Migration
  def change
    create_table :monitor_node_users do |t|
      t.integer :monitor_node_id, null:false
      t.integer :user_id, null:false
      t.timestamps
    end
  end
end
