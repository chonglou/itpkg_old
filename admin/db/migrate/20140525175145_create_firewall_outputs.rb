class CreateFirewallOutputs < ActiveRecord::Migration
  def change
    create_table :firewall_outputs do |t|
      t.integer :host_id, null: false
      t.string :name, null: false
      t.string :keyword, null: false
      t.string :weekly, null: false
      t.time :startup, null: false
      t.time :shutdown, null: false
      t.datetime :created, null: false
    end
  end
end
