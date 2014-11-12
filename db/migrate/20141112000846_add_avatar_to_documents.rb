class AddAvatarToDocuments < ActiveRecord::Migration
  def change
    add_column :documents, :avatar, :string, null:false
  end
end
