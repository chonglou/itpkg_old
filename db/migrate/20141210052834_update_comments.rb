class UpdateComments < ActiveRecord::Migration
  def up
    drop_table :comments

    create_table :story_comments do |t|
      t.text :content

      t.belongs_to :user
      t.belongs_to :story

      t.timestamps
    end

    create_table :task_comments do |t|
      t.text :content

      t.belongs_to :user
      t.belongs_to :task

      t.timestamps
    end

    add_index :story_comments, :user_id
    add_index :story_comments, :story_id

    add_index :task_comments, :user_id
    add_index :task_comments, :task_id
  end

  def down
    drop_table :task_comments
    drop_table :story_comments

    create_table :comments do |t|
      t.text :content

      t.belongs_to :project
      t.belongs_to :task
      t.belongs_to :story
      t.belongs_to :user

      t.timestamps
    end
  end
end
