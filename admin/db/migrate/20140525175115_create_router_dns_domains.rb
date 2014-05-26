class CreateRouterDnsDomains < ActiveRecord::Migration
  def change
    create_table :router_dns_domains do |t|
      t.string :name, null: false
      t.integer :host_id, null: false
      t.integer :ttl, null: false, default: 300
      t.datetime :created, null: false
    end
  end
end
