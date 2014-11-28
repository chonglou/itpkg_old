class CreateNodeTypes < ActiveRecord::Migration
  def change
    create_table :node_types do |t|
      t.string :name, null:false
      t.integer :creator_id, null:false
      t.timestamps
    end
    add_index :node_types, [:name, :creator_id], unique:true
  end
end
