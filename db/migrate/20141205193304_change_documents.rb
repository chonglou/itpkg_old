class ChangeDocuments < ActiveRecord::Migration
  def change
    add_column :documents, :size, :integer, null:false, default:0
    add_column :documents, :details, :text
    change_column :documents, :ext, :string,limit: 5, null:true
    remove_column :documents, :title
  end
end
