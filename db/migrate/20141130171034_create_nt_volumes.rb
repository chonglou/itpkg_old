class CreateNtVolumes < ActiveRecord::Migration
  def change
    create_table :nt_volumes do |t|
      t.integer :node_type_id, null:false
      t.integer :s_path, null:false
      t.integer :d_path, null:false
      t.timestamps
    end

    add_index :nt_volumes, [:node_type_id, :d_path], unique:true
  end
end
