class CreateOpsGroups < ActiveRecord::Migration
  def change
    create_table :ops_groups do |t|
      t.string :name, null:false
      t.timestamps
    end

    add_index :ops_groups, :name, unique: false
  end
end
