class CreateTeamProjectMembers < ActiveRecord::Migration
  def change
    create_table :team_project_members do |t|
      t.integer :project_id, null:false
      t.integer :member_id, null:false
      t.integer :flag, null:false, limit:2, default:0
      t.timestamps
    end
  end
end
