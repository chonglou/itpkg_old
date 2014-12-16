class CreateLoggingSearches < ActiveRecord::Migration
  def change
    create_table :logging_searches do |t|
      t.string :keyword, null:false, default:'.*'
      t.string :host, null:false, default:'.*'
      t.string :hostname, null:false, default:'.*'
      t.string :tag, null:false, default:'.*'
      t.timestamps
    end
  end
end
