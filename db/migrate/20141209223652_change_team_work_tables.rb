class ChangeTeamWorkTables < ActiveRecord::Migration
  def change
    add_column :tasks, :level, :integer, null:false, default:0,limit:2

  end
end
