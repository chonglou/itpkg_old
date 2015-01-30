class RemoveCreateIdFromProject < ActiveRecord::Migration
  def change
    remove_column :projects, :creator_id
    drop_table    :projects_users
  end
end
