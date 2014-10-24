class CreateMachineNodes < ActiveRecord::Migration
  def change
    create_table :machine_nodes do |t|
      t.integer :creator, null:false
      t.string :name, null:false, limit:32
      t.string :uid, null:false, limit:36
      t.string :title
      t.text :config, null:false
      t.integer :status, null:false, default:0, limit:2
      t.timestamps
    end
    add_index :machine_nodes, :name
    add_index :machine_nodes, :uid, unique: true
  end
end
