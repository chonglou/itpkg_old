class CreateDnsHosts < ActiveRecord::Migration
  def change
    create_table :dns_hosts do |t|
      t.string :name, null: false
      t.text :details
      t.integer :user_id, null: false
      t.integer :client_id, null: false
      t.datetime :created, null: false
    end
  end
end
