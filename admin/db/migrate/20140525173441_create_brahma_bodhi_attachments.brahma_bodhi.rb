# This migration comes from brahma_bodhi (originally 20140511172723)
class CreateBrahmaBodhiAttachments < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_attachments do |t|
      t.integer :user_id, null:false
      t.integer :size, null:false
      t.string :file_name, null:false
      t.string :content_type, null:false
      t.string :original_filename, null:false
      t.binary :content, null: false, limit:10.megabyte
      t.datetime :last_edit, null: false
      t.datetime :created, null: false
    end
    add_index :brahma_bodhi_attachments, :file_name, unique: true
  end
end
