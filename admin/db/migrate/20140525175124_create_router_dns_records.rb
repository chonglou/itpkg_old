class CreateRouterDnsRecords < ActiveRecord::Migration
  def change
    create_table :router_dns_records do |t|
      t.string :name, null:false
      t.integer :flag, null:false, default:0, limit: 2
      t.string :value, null:false
      t.integer :domain_id, null:false
      t.integer :priority, null:false, default:0, limit: 2
      t.datetime :created
    end
  end
end
