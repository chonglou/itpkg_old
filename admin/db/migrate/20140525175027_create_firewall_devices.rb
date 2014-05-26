class CreateFirewallDevices < ActiveRecord::Migration
  def change
    create_table :firewall_devices do |t|
      t.string :mac, null: false
      t.string :name, null: false
      t.integer :state, null: false, limit: 2, default: 0
      t.integer :host_id, null: false
      t.text :details
      t.integer :ip, null: false, limit: 2
      t.datetime :created, null: false
    end
  end
end
