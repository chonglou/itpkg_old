class CreateTeamActivities < ActiveRecord::Migration
  def change
    create_table :team_activities do |t|
      t.integer :issue_id, null:false
      t.integer :user_id, null:false
      t.text :message, null:false
      t.timestamps
    end
  end
end
