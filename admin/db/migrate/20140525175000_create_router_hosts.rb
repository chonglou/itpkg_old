class CreateRouterHosts < ActiveRecord::Migration
  def change
    create_table :router_hosts do |t|
      t.integer :user_id, null: false
      t.integer :client_id, null: false
      t.string :name, null: false
      t.text :details
      t.string :wan, null: false
      t.string :lan, null: false
      t.string :dmz
      t.datetime :created, null: false
    end
  end
end
