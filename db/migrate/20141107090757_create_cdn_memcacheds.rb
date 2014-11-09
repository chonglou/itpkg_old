class CreateCdnMemcacheds < ActiveRecord::Migration
  def change
    create_table :cdn_memcacheds do |t|
      t.string :name, null: false
      t.string :ip, null: false
      t.integer :port, null: false
      t.integer :weight, null: false, default: 0
      t.timestamps
    end
    add_index :cdn_memcacheds, :ip
  end
end
