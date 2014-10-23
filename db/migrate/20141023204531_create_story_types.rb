class CreateStoryTypes < ActiveRecord::Migration
  def change
    create_table :story_types do |t|
      t.integer :story_id, null:false
      t.integer :s_type_id, null:false
      t.timestamps
    end
  end
end
