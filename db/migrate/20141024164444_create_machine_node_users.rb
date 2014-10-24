class CreateMachineNodeUsers < ActiveRecord::Migration
  def change
    create_table :machine_node_users do |t|

      t.integer :machine_node_id, null:false
      t.integer :user_id, null:false
      t.timestamps
    end
  end
end
