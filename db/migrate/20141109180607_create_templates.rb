class CreateTemplates < ActiveRecord::Migration
  def change
    create_table :templates do |t|
      t.string :flag, null: false, limit: 32
      t.string :name, null: false
      t.text :body, null: false
      t.column :mode, 'char(3)', null: false, default: '400'
      t.string :owner, null: false, default: 'root:root', limit: 16
      t.integer :version, null: false, default: 0
      t.timestamps
    end
    add_index :templates, :name
    add_index :templates, :owner
    add_index :templates, :mode
    add_index :templates, :flag
    add_index :templates, [:flag, :name], unique: true
  end
end
