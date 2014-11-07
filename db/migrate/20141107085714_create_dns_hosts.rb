class CreateDnsHosts < ActiveRecord::Migration
  def change
    create_table :dns_hosts do |t|
      t.string :name, null:false
      t.string :ip, null:false
      t.string :password, null:false
      t.integer :weight, null:false,default:0
      t.timestamps
    end
    add_index :dns_hosts, :ip, unique:true
  end
end
