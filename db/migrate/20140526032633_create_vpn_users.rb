class CreateVpnUsers < ActiveRecord::Migration
  def change
    create_table :vpn_users do |t|
      t.integer :host_id, null: false
      t.string :username, null: false
      t.string :password, null: false
      t.integer :state, null: false, limit: 2, default: 0
      t.datetime :created, null: false
    end
  end
end
