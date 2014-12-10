class ChangeRbac < ActiveRecord::Migration
  def change
    drop_table :permissions
  end
end
