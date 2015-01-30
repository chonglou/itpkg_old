class ChangeTeamWorkTables < ActiveRecord::Migration
  def change
    add_column :tasks, :level, :integer, null:false, default:0,limit:2

    drop_table :project_users


    create_table :projects_users, id: false do |t|
      t.belongs_to :project
      t.belongs_to :user

      t.timestamps
    end
  end
end
