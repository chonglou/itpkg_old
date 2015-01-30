class CreateMonitorNodes < ActiveRecord::Migration
  def change
    create_table :monitor_nodes do |t|
      t.integer :flag, null: false, default: 0, limit: 2
      t.string :name, null: false
      t.string :uuid, null: false, limit: 36
      t.text :encrypted_cfg, null: false
      t.string :encrypted_cfg_salt, null: false
      t.string :encrypted_cfg_iv, null: false
      t.timestamps
    end
    add_index :monitor_nodes, :uuid, unique: true
    add_index :monitor_nodes, :name
  end
end
