class CreateProjectUsers < ActiveRecord::Migration
  def change
    create_table :project_users do |t|
      t.integer :project_id, null:false, default:0
      t.integer :member_id, null:false, default:0
      t.timestamps
    end
  end
end
