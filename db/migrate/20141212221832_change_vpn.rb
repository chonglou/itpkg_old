class ChangeVpn < ActiveRecord::Migration
  def change
    drop_table :vpn_users
    create_table :vpn_users do |t|
      t.string :name, null: false
      t.string :email
      t.string :phone
      t.string :password, null: false
      t.boolean :online, default: false
      t.boolean :enable, default: false
      t.date :start_date, null: false
      t.date :end_date, null: false
      t.timestamps
    end
    add_index :vpn_users, :name, unique: true

    drop_table :vpn_logs
    create_table :vpn_logs do |t|
      t.string :user, null:false
      t.string :trusted_ip, limit:32
      t.string :trusted_port, limit:16
      t.string :remote_ip, limit:32
      t.string :remote_port, limit:16
      t.string :message
      t.datetime :start_time, null:false
      t.datetime :end_time, null:false, default:'0000-00-00 00:00:00'
      t.float :received, null:false, default:0
      t.float :send, null:false, default:0
    end
    add_index :vpn_logs, :user
  end
end
