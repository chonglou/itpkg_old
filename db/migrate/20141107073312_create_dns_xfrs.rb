class CreateDnsXfrs < ActiveRecord::Migration
  def change
    create_table :dns_xfrs do |t|
      t.string :zone, null:false
      t.string :client, null:false
      t.string :code, limit:1, default:'*'
      t.timestamps
    end
    add_index :dns_xfrs, :zone
    add_index :dns_xfrs, :client
    add_index :dns_xfrs, :code
    add_index :dns_xfrs, [:zone, :client, :code], unique:true
  end
end
