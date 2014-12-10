class RemoveCreator < ActiveRecord::Migration
  def change
    remove_column :wikis, :creator_id
    remove_column :wikis, :author_id
    remove_column :documents, :creator_id
    remove_column :repositories, :creator_id
  end
end
