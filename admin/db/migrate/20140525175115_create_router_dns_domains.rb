class CreateRouterDnsDomains < ActiveRecord::Migration
  def change
    create_table :router_dns_domains do |t|

      t.timestamps
    end
  end
end
