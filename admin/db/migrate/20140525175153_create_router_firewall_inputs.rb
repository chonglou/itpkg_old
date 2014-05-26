class CreateRouterFirewallInputs < ActiveRecord::Migration
  def change
    create_table :router_firewall_inputs do |t|
      t.string :name, null: false
      t.integer :host_id, null: false
      t.integer :s_port, null: false
      t.integer :protocol, null: false, default: 0
      t.string :s_ip, null: false, default: '*'
      t.datetime :created, null: false
    end
  end
end
