class CreateDnsCounts < ActiveRecord::Migration
  def change
    create_table :dns_counts do |t|
      t.string :zone, null:false
      t.integer :count, null:false, default:0
      t.integer :code, default:0
      t.timestamps
    end

    add_index :dns_counts, :zone
  end
end
