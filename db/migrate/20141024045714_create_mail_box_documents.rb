class CreateMailBoxDocuments < ActiveRecord::Migration
  def change
    create_table :mail_box_documents do |t|
      t.integer :document_id, null:false
      t.integer :mail_box_id, null:false
      t.timestamps
    end
  end
end
