class ChangeMailBox < ActiveRecord::Migration
  def change
    drop_table :mail_boxes
    drop_table :mail_boxes_documents
  end
end
