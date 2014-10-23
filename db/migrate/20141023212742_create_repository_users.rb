class CreateRepositoryUsers < ActiveRecord::Migration
  def change
    create_table :repository_users do |t|
      t.integer :repository_id, null:false
      t.integer :user_id, null:false
      t.timestamps
    end
  end
end
