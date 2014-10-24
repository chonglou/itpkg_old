class CreateRepositoryLogs < ActiveRecord::Migration
  def change
    create_table :repository_logs do |t|
      t.integer :repository_id, null: false
      t.string :branch, null: false
      t.string :name, null: false
      t.string :email, null: false
      t.string :message, null: false
      t.datetime :created, null: false
    end
  end
end
