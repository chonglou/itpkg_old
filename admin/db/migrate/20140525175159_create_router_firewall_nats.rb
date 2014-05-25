class CreateRouterFirewallNats < ActiveRecord::Migration
  def change
    create_table :router_firewall_nats do |t|

      t.timestamps
    end
  end
end
