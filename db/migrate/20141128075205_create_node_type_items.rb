class CreateNodeTypeItems < ActiveRecord::Migration
  def change
    create_table :node_type_items do |t|
      t.string :name, null:false
      t.integer :flag, null:false
      t.integer :node_type_id, null:false
      t.timestamps
    end
    add_index :node_type_items, [:name, :node_type_id], unique:true
  end
end
