class CreateNodeTypes < ActiveRecord::Migration
  def change
    create_table :node_types do |t|
      t.string :name, null:false
      t.text :dockerfile, null:false
      t.string :ports
      t.string :volumes
      t.text :vars
      t.integer :creator_id, null:false
      t.timestamps
    end
    add_index :node_types, :name, unique:true
  end
end
