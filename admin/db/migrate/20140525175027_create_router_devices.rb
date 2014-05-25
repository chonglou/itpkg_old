class CreateRouterDevices < ActiveRecord::Migration
  def change
    create_table :router_devices do |t|

      t.timestamps
    end
  end
end
