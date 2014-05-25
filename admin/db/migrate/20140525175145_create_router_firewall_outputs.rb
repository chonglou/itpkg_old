class CreateRouterFirewallOutputs < ActiveRecord::Migration
  def change
    create_table :router_firewall_outputs do |t|

      t.timestamps
    end
  end
end
