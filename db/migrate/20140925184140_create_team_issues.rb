class CreateTeamIssues < ActiveRecord::Migration
  def change
    create_table :team_issues do |t|
      t.string :name, null:false
      t.text :details, null:false

      t.integer :project_id, null:false
      t.integer :creator_id, null:false
      t.integer :worker_id, null:false

      t.integer :point, null:false
      t.integer :value, null:false

      t.datetime :start_up, null:false
      t.datetime :shut_down, null:false

      t.integer :status, null:false, default:0, limit:2
      t.integer :flag, null:false, default:0, limit:2
      t.timestamps
    end

  end
end
