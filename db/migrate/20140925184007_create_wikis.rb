class CreateWikis < ActiveRecord::Migration
  def change
    create_table :wikis do |t|
      t.string :title, null:false
      t.text :body, null:false
      t.integer :version, null:false, default:0
      t.timestamps
    end
  end
end
