class CreateLoggingSearches < ActiveRecord::Migration
  def change
    create_table :logging_searches do |t|
      t.string :name, null:false
      t.string :message, null:false, default:'.*'
      t.string :vip, null:false, default:'.*'
      t.string :hostname, null:false, default:'.*'
      t.string :tag, null:false, default:'.*'
      t.timestamps
    end
  end
end
