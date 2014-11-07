class CreateCdnServers < ActiveRecord::Migration
  def change
    create_table :cdn_servers do |t|
      t.string :address, null:false
      t.integer :weight, null:false, default:0
      t.timestamps
    end
    add_index :cdn_servers, :address
  end
end
