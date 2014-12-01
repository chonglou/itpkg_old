class CreateNtVars < ActiveRecord::Migration
  def change
    create_table :nt_vars do |t|
      t.integer :node_type_id, null:false
      t.string :name, null:false
      t.text :def_v
      t.timestamps
    end
    add_index :nt_vars, [:node_type_id, :name], unique:true
  end
end
