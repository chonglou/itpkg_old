class RemoveRequesterIdFromStory < ActiveRecord::Migration
  def change
    remove_column :stories, :requester_id
  end
end
