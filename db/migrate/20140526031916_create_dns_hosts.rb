class CreateDnsHosts < ActiveRecord::Migration
  def change
    create_table :dns_hosts do |t|
      t.integer :client_id, null: false
      t.string :dns1, null:false, default: '8.8.8.8'
      t.string :dns2, null:false, default: '8.8.4.4'
      t.datetime :created, null: false
    end
  end
end
