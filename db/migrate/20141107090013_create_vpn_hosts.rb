class CreateVpnHosts < ActiveRecord::Migration
  def change
    create_table :vpn_hosts do |t|
      t.string :name, null:false
      t.string :ip, null:false
      t.string :network, null:false
      t.string :routes, null:false
      t.string :dns, null:false
      t.string :password, null:false
      t.integer :weight, null:false,default:0
      t.timestamps
    end
    add_index :vpn_hosts, :ip, unique:true
  end
end
