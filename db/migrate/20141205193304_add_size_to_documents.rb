class AddSizeToDocuments < ActiveRecord::Migration
  def change
    add_column :documents, :size, :integer, null:false, default:0
  end
end
