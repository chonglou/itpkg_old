class CreateNtVolumes < ActiveRecord::Migration
  def change
    create_table :nt_volumes do |t|
      t.integer :node_type_id, null:false
      t.string :s_path, null:false
      t.string :t_path, null:false
      t.timestamps
    end

    add_index :nt_volumes, [:node_type_id, :t_path], unique:true
  end
end
