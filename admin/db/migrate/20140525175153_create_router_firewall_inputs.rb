class CreateRouterFirewallInputs < ActiveRecord::Migration
  def change
    create_table :router_firewall_inputs do |t|

      t.timestamps
    end
  end
end
