class CreateDnsAcls < ActiveRecord::Migration
  def change
    create_table :dns_acls do |t|
      t.string :country
      t.string :region, null:false, default:'*'
      t.string :city, null:false, default:'*'
      t.timestamps
    end
    add_index :dns_acls, :country
    add_index :dns_acls, :region
    add_index :dns_acls, :city
    add_index :dns_acls, [:country, :region,:city], unique:true
  end
end
