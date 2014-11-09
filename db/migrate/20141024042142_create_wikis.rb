class CreateWikis < ActiveRecord::Migration
  def change
    create_table :wikis do |t|
      t.integer :project_id, null: false
      t.integer :creator_id, null: false
      t.string :title, null: false
      t.text :body, null: false
      t.integer :status, null: false, default: 0, limit: 2
      t.integer :author_id, null: false
      t.datetime :created, null: false
    end
    add_index :wikis, :title
  end
end
