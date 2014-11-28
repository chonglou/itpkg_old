class CreateNodeUsers < ActiveRecord::Migration
  def change
    create_table :node_users do |t|
      t.integer :node_id, null:false
      t.integer :user_id, null:false
      t.timestamps
    end
    add_index :node_users, [:node_id, :user_id], unique:true
  end
end
