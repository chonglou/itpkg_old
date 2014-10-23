class CreateStories < ActiveRecord::Migration
  def change
    create_table :stories do |t|
      t.string :title, null:false
      t.integer :project_id, null:false
      t.integer :story_type_id, null:false
      t.integer :point, null:false,default:0
      t.integer :requester_id, null:false
      t.integer :status, null:false, default:0, limit:1
      t.text :description
      t.timestamps
    end
  end
end
