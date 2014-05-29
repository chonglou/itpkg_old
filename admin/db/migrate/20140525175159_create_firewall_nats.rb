class CreateFirewallNats < ActiveRecord::Migration
  def change
    create_table :firewall_nats do |t|
      t.string :name, null: false
      t.integer :host_id, null: false
      t.integer :s_port, null: false
      t.integer :protocol, null: false, default: 0, limit: 2
      t.integer :d_port, null: false
      t.integer :d_ip, null: false
      t.datetime :created, null: false
    end
  end
end
