class CreateRssSites < ActiveRecord::Migration
  def change
    create_table :rss_sites do |t|
      t.string :title, null:false
      t.string :url, null:false
      t.string :logo
      t.integer :space, null:false, default:24*60
      t.timestamps
    end
    add_index :rss_sites, :url, unique: true
  end
end
