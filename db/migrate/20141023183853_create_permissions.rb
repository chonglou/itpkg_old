class CreatePermissions < ActiveRecord::Migration
  def change
    create_table :permissions do |t|
      t.string :resource, null: false
      t.string :role, null: false
      t.string :operation, null: false

      t.date :start_date, null: false, default: '9999-12-31'
      t.date :end_date, null: false, default: '1000-01-01'

      t.timestamps
    end

    add_index :permissions, :role
    add_index :permissions, :resource
    add_index :permissions, :operation

    add_index :permissions, [:role, :resource, :operation], unique:true

  end
end
