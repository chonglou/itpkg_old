class AddToDocuments < ActiveRecord::Migration
  def change
    add_column :documents, :size, :integer, null:false, default:0
    add_column :documents, :detail, :text
  end
end
