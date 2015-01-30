class ChangeLoggingNode < ActiveRecord::Migration
  def change
    remove_column :logging_nodes, :uuid
    remove_column :logging_nodes, :encrypted_cfg
    remove_column :logging_nodes, :encrypted_cfg_salt
    remove_column :logging_nodes, :encrypted_cfg_iv
    remove_column :monitor_nodes, :uuid

    add_column :logging_nodes, :vip, :string, null: false, limit: 15
    add_index :logging_nodes, :vip, unique: true

    add_column :monitor_nodes, :vip, :string, null: false, limit: 15
    add_index :monitor_nodes, :vip, unique: true


  end

end
