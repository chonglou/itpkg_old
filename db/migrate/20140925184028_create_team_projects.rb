class CreateTeamProjects < ActiveRecord::Migration
  def change
    create_table :team_projects do |t|
      t.integer :manager_id, null:false
      t.string :name, null:false
      t.string :title, null:false
      t.text :detail, null:false
      t.integer :status, null:false, limit:2
      t.timestamps
    end
    add_index :team_projects, :name, unique: true
  end
end
