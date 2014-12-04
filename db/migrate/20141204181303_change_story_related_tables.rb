class ChangeStoryRelatedTables < ActiveRecord::Migration
  def up
    drop_table :story_tags
    drop_table :story_types
    drop_table :s_tags
    drop_table :s_types

    rename_table :s_tasks, :tasks

    remove_column :stories, :story_type_id

    create_table :story_tags do |t|
      t.string :name
      t.string :icon

      t.belongs_to :project

      t.timestamps
    end

    create_table :stories_story_tags, id: false do |t|
      t.belongs_to :story
      t.belongs_to :story_tag

      t.timestamps
    end

    create_table :story_types do |t|
      t.string :name
      t.string :icon

      t.belongs_to :project

      t.timestamps
    end

    create_table :stories_story_types, id: false do |t|
      t.belongs_to :story
      t.belongs_to :story_type

      t.timestamps
    end
  end

  def down
    drop_table :story_tags
    drop_table :story_types
    drop_table :stories_story_tags
    drop_table :stories_story_types

    rename_table :tasks, :s_tasks

    add_column :stories, :story_type_id, :integer

    create_table :s_tags do |t|
      t.string :name
      t.string :icon

      t.belongs_to :project

      t.timestamps
    end

    create_table :story_tags do |t|
      t.belongs_to :story
      t.belongs_to :s_tag

      t.timestamps
    end

    create_table :s_types do |t|
      t.string :name
      t.string :icon

      t.belongs_to :project

      t.timestamps
    end

    create_table :story_types do |t|
      t.belongs_to :story
      t.belongs_to :s_type

      t.timestamps
    end
  end
end
