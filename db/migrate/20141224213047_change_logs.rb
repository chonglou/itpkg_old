class ChangeLogs < ActiveRecord::Migration
  def change
    remove_column :logs, :updated_at
    remove_column :logs, :created_at
    add_column :logs, :created, :timestamp, null:false
  end
end
