class CreateNotices < ActiveRecord::Migration
  def change
    create_table :notices do |t|
      t.integer :user_id, null:false
      t.text :body, null:false
      t.timestamps
    end
  end
end
