class CreateDnsXfrs < ActiveRecord::Migration
  def change
    create_table :dns_xfrs do |t|
      t.string :zone, null: false
      t.string :client, null: false
      t.integer :code, default: 0
      t.timestamps
    end
    add_index :dns_xfrs, :zone
    add_index :dns_xfrs, :client
    add_index :dns_xfrs, [:zone, :client], unique: true
  end
end
