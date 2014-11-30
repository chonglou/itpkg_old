class CreateNodes < ActiveRecord::Migration
  def change
    create_table :nodes do |t|
      t.integer :creator_id, null: false
      t.integer :node_type_id, null: false
      t.integer :name, null: false

      t.text :encrypted_keys, null: false
      t.string :encrypted_keys_salt, null: false
      t.string :encrypted_keys_iv, null: false

      t.text :encrypted_cfg, null: false
      t.string :encrypted_cfg_salt, null: false
      t.string :encrypted_cfg_iv, null: false

      t.integer :status, null:false, default:0
      t.string :nid, null:false
      t.timestamps
    end
    add_index :nodes, :nid, unique:true
  end
end
