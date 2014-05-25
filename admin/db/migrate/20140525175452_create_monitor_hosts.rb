class CreateMonitorHosts < ActiveRecord::Migration
  def change
    create_table :monitor_hosts do |t|

      t.timestamps
    end
  end
end
