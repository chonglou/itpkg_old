class ChangeMonitorNodes < ActiveRecord::Migration
  def change
    change_table :monitor_nodes do |t|
      t.integer :status, null: false, default: 0, limit: 2
      t.integer :space, null:false, default:60
      t.datetime :next_run, null:false, default:'9999-12-31 23:59:59'
    end
  end
end
