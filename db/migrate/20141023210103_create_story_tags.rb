class CreateStoryTags < ActiveRecord::Migration
  def change
    create_table :story_tags do |t|
      t.integer :story_id, null:false
      t.integer :s_tag_id, null:false
      t.timestamps
    end
  end
end
