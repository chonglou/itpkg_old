class CreateRouterHosts < ActiveRecord::Migration
  def change
    create_table :router_hosts do |t|

      t.timestamps
    end
  end
end
