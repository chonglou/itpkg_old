# This migration comes from brahma_bodhi (originally 20140628044945)
class CreateBrahmaBodhiAttachments < ActiveRecord::Migration
  def change
    create_table :brahma_bodhi_attachments do |t|
      t.integer :user_id, null:false
      t.timestamps
    end
  end
end
