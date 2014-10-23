class CreatePermissions < ActiveRecord::Migration
  def change
    create_table :permissions do |t|
      t.string :resource, null: false
      t.string :role, null: false
      t.string :operation, null: false

      t.date :startup, null: false
      t.date :shutdown, null: false

      t.timestamps
    end

    add_index :permissions, :role
    add_index :permissions, :resource
    add_index :permissions, :operation

  end
end
