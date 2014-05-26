class CreateCdnHosts < ActiveRecord::Migration
  def change
    create_table :cdn_hosts do |t|
      t.integer :client_id, null: false
      t.datetime :created, null: false
    end
  end
end
