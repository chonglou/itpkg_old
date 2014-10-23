class CreateVpnLogs < ActiveRecord::Migration
  def change
    create_table :vpn_logs do |t|
      t.string :flag, limit:1, null:false,default:'O'
      t.string :username, null: false, limit: 32
      t.string :message, null: false
      t.timestamp :created, null:false
    end
    add_index :vpn_logs, :username
  end
end
