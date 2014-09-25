class CreateTeamDocuments < ActiveRecord::Migration
  def change
    create_table :team_documents do |t|
      t.integer :project_id, null:false
      t.integer :user_id, null:false
      t.string :name, null:false
      t.string :ext, null:false
      t.string :title, null:false
      t.integer :size, null:false
      t.timestamps
    end

    add_index :team_documents, :name, unique:true
  end
end
