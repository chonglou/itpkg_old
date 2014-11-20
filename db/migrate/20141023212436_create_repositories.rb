class CreateRepositories < ActiveRecord::Migration
  def change
    create_table :repositories do |t|
      t.integer :creator_id, null: false
      t.string :name, null: false, limit: 16
      t.string :title, null:false
      t.timestamps
    end
    add_index :repositories, :name, unique: true
  end
end
