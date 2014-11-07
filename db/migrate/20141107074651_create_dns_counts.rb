class CreateDnsCounts < ActiveRecord::Migration
  def change
    create_table :dns_counts do |t|
      t.string :zone, null:false
      t.integer :count, null:false, default:0
      t.string :code, limit:1, default:'*'
      t.timestamps
    end
    add_index :dns_counts, :code
    add_index :dns_counts, :zone
    add_index :dns_counts, [:zone, :code], unique:true
  end
end
