class CreateRouterFirewallNats < ActiveRecord::Migration
  def change
    create_table :router_firewall_nats do |t|
      t.string :name, null: false
      t.integer :host_id, null: false
      t.integer :s_port, null: false
      t.integer :protocol, null: false, default: 0
      t.integer :d_port, null: false
      t.integer :d_ip, null: false
      t.datetime :created, null: false
    end
  end
end
