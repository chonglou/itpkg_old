class CreateSettings < ActiveRecord::Migration
  def change
    create_table :settings do |t|
      t.string :key, null:false
      t.text :val, null:false
      t.timestamps
    end
    add_index :settings, :key, unique:true
  end
end
