class ChangeDocumentName < ActiveRecord::Migration
  def change
    change_column :documents, :name, :string, limit: nil
  end
end
