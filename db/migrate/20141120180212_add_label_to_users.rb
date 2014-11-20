class AddLabelToUsers < ActiveRecord::Migration
  def change
    add_column :users, :label, :string, null:false
    add_index :users, :label, unique:true
  end
end
