class CreateProjects < ActiveRecord::Migration
  def change
    create_table :projects do |t|
      t.string :name, null:false
      t.text :details
      t.integer :creator_id, null:false
      t.timestamps
    end
    add_index :projects, :name
  end
end
