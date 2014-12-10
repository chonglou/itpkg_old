class ChangeWiki < ActiveRecord::Migration
  def change
    remove_column :wikis, :project_id
    remove_column :wikis, :created

    change_table :wikis do |t|
      t.timestamps
    end
  end
end
