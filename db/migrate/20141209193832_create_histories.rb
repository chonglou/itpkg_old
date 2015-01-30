class CreateHistories < ActiveRecord::Migration
  def change
    create_table :histories do |t|
      t.string :url, null:false
      t.text :data, null:false
      t.timestamps
    end
    add_index :histories, :url
  end
end
