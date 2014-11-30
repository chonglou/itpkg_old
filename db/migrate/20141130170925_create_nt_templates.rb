class CreateNtTemplates < ActiveRecord::Migration
  def change
    create_table :nt_templates do |t|
      t.string :name, null: false
      t.text :body, null: false
      t.column :mode, 'char(4)', null: false, default: '400'
      t.string :owner, null: false, default: 'root:root', limit: 16
      t.integer :node_type_id, null:false
      t.integer :version, null: false, default: 0

      t.timestamps
    end
    add_index :templates, :name
    add_index :templates, :owner
    add_index :templates, :mode
    add_index :templates, [:node_type_id, :name], unique: true
  end
end
