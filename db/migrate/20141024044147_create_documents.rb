class CreateDocuments < ActiveRecord::Migration
  def change
    create_table :documents do |t|
      t.integer :project_id, null: false
      t.integer :creator_id, null: false
      t.string :title, null: false
      t.string :name, null: false, limit: 36
      t.string :ext, null: false, limit: 5
      t.integer :status, null: false, default: 0, limit: 2
      t.timestamps
    end
    add_index :documents, :title
  end
end
