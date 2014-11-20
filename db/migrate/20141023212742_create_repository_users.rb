class CreateRepositoryUsers < ActiveRecord::Migration
  def change
    create_table :repository_users do |t|
      t.integer :repository_id, null: false
      t.integer :user_id, null: false
      t.integer :certificate_id, null: false, default:0
      t.boolean :writable, null:false, default:false
      t.timestamps
    end
    add_index :repository_users, [:user_id, :repository_id], unique:true
  end
end
