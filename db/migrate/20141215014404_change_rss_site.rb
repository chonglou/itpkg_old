class ChangeRssSite < ActiveRecord::Migration
  def change
    drop_table :rss_sites
    create_table :rss_sites do |t|
      t.string :url, null:false
      t.string :title
      t.string :logo
      t.datetime :last_sync
      t.timestamps
    end
    add_index :rss_sites, :url, unique:true
  end
end
