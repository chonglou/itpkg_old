class CreateNtTemplates < ActiveRecord::Migration
  def change
    create_table :nt_templates do |t|
      t.string :name, null: false
      t.text :body, null: false
      t.column :mode, 'char(3)', null: false, default: '400'
      t.string :owner, null: false, default: 'root:root', limit: 16
      t.integer :node_type_id, null:false
      t.integer :version, null: false, default: 0

      t.timestamps
    end
    add_index :nt_templates, :name
    add_index :nt_templates, :owner
    add_index :nt_templates, :mode
    add_index :nt_templates, [:node_type_id, :name], unique: true
  end
end
