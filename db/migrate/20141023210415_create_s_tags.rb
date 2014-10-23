class CreateSTags < ActiveRecord::Migration
  def change
    create_table :s_tags do |t|
      t.integer :project_id, null:false
      t.string :name, null:false
      t.string :icon, null:false
      t.timestamps
    end
    add_index :s_tags, :name
  end
end
