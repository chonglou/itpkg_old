class CreateMonitorGroups < ActiveRecord::Migration
  def change
    create_table :monitor_groups do |t|
      t.string :name, null:false
      t.timestamps
    end
    add_index :monitor_groups, :name, unique:true
  end
end
