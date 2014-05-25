class CreateRouterDnsRecords < ActiveRecord::Migration
  def change
    create_table :router_dns_records do |t|

      t.timestamps
    end
  end
end
