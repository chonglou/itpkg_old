class CreateIssues < ActiveRecord::Migration
  def change
    create_table :issues do |t|
      t.integer :project_id, null:false
      t.string :title, null:false
      t.text :body, null:false
      t.integer :user_id, null:false
      t.integer :flag, null:false, default:0, limit:1
      t.integer :state, null:false, default:0, limit:2
      t.timestamps
    end
  end
end
