# This migration comes from brahma_bodhi (originally 20140628045726)
class AddFileToBrahmaBodhiAttachments < ActiveRecord::Migration
  def change
    add_column :brahma_bodhi_attachments, :file, :string
  end
end
