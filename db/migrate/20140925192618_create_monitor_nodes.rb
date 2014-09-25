class CreateMonitorNodes < ActiveRecord::Migration
  def change
    create_table :monitor_nodes do |t|
      t.integer :group_id, null:false
      t.string :title, null:false
      t.text :profile, null:false
      t.integer :flag, default:0, null:false, limit:2
      t.timestamps
    end

  end
end
