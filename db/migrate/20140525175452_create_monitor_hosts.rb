class CreateMonitorHosts < ActiveRecord::Migration
  def change
    create_table :monitor_hosts do |t|
      t.integer :client_id, null: false
      t.datetime :created, null: false
    end
  end
end
