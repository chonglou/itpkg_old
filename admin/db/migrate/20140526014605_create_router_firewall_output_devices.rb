class CreateRouterFirewallOutputDevices < ActiveRecord::Migration
  def change
    create_table :router_firewall_output_devices do |t|
      t.integer :output_id, null:false
      t.integer :device_id, null:false
      t.datetime :created, null:false
    end
  end
end
