class CreateComments < ActiveRecord::Migration
  def change
    create_table :comments do |t|
      t.integer :user_id, null: false

      t.integer :project_id
      t.integer :task_id
      t.integer :story_id

      t.text :content, null: false
      t.timestamps
    end

  end
end
