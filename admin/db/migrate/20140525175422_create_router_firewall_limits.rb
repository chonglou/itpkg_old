class CreateRouterFirewallLimits < ActiveRecord::Migration
  def change
    create_table :router_firewall_limits do |t|

      t.timestamps
    end
  end
end
