class UpdateTaskDetailsType < ActiveRecord::Migration
  def up
    change_column :tasks, :details, :text
  end

  def down
    change_column :tasks, :details, :string
  end
end
