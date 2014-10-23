class CreateRepositoryLogs < ActiveRecord::Migration
  def change
    create_table :repository_logs do |t|
      t.integer :repository_id, null:false
      t.integer :user_id, null:false
      t.text :message, null:false
      t.timestamps
    end
  end
end
