class CreateLogs < ActiveRecord::Migration
  def change
    create_table :logs do |t|
      t.integer :user_id, null:false
      t.string :message, null:false
      t.timestamps
    end
  end
end
