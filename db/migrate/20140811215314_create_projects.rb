class CreateProjects < ActiveRecord::Migration
  def change
    create_table :projects do |t|
      t.string :name, null:false
      t.string :title, null:false
      t.text :details, null:false
      t.timestamps
    end
  end
end
