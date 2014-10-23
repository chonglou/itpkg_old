class CreateStoryOwners < ActiveRecord::Migration
  def change
    create_table :story_owners do |t|
      t.integer :story_id
      t.integer :user_id
      t.timestamps
    end
  end
end
