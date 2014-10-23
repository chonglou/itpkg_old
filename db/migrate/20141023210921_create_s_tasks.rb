class CreateSTasks < ActiveRecord::Migration
  def change
    create_table :s_tasks do |t|
      t.integer :story_id, null:false
      t.string :details,null:false
      t.timestamps
    end
  end
end
