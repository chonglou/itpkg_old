class CreateNtPorts < ActiveRecord::Migration
  def change
    create_table :nt_ports do |t|
      t.integer :node_type_id, null:false
      t.integer :s_port, null:false
      t.boolean :tcp, null:false, default:true
      t.integer :d_port, null:false
      t.timestamps
    end
    add_index :node_type_ports, [:node_type_id, :tcp, :d_port], unique:true
  end
end
