class CreateCdnNginxMemcacheds < ActiveRecord::Migration
  def change
    create_table :cdn_nginx_memcacheds do |t|
      t.integer :nginx_id, null: false
      t.integer :memcached_id, null: false
      t.timestamps
    end
  end
end
