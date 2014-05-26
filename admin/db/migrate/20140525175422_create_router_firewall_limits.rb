class CreateRouterFirewallLimits < ActiveRecord::Migration
  def change
    create_table :router_firewall_limits do |t|
      t.string :name, null: false
      t.integer :host_id, null: false
      t.integer :max_up, null: false
      t.integer :max_down, null: false
      t.integer :min_down, null: false
      t.integer :min_up, null: false
      t.datetime :created, null: false
    end
  end
end
