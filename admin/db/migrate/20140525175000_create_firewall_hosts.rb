class CreateFirewallHosts < ActiveRecord::Migration
  def change
    create_table :firewall_hosts do |t|
      t.integer :client_id, null: false
      t.string :wan, null: false
      t.string :lan, null: false
      t.string :dmz
      t.datetime :created, null: false
    end
  end
end
