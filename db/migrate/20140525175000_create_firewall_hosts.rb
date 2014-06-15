class CreateFirewallHosts < ActiveRecord::Migration
  def change
    create_table :firewall_hosts do |t|
      t.integer :client_id, null: false
      t.text :wan, null: false
      t.text :lan, null: false
      t.text :dmz, null:false
      t.datetime :created, null: false
    end
  end
end
