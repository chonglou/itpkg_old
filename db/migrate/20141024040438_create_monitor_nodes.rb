class CreateMonitorNodes < ActiveRecord::Migration
  def change
    create_table :monitor_nodes do |t|
      t.integer :creator, null:false
      t.string :name, null:false, limit:32
      t.string :title
      t.text :config, null:false
      t.integer :status, null:false, default:0, limit:2
      t.integer :flag, null:false, default:0, limit:2
      t.timestamps
    end
  end
end
