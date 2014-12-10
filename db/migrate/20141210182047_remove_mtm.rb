class RemoveMtm < ActiveRecord::Migration
  def change
    drop_table :mail_box_documents
    drop_table :wiki_users
    drop_table :repository_users

    create_table :mail_boxes_documents, id: false do |t|
      t.belongs_to :mail_boxes
      t.belongs_to :documents

      t.timestamps
    end

    create_table :wikis_users, id: false do |t|
      t.belongs_to :wikis
      t.belongs_to :wikis

      t.timestamps
    end

  end
end
