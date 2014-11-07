class CreateCdnNginxServers < ActiveRecord::Migration
  def change
    create_table :cdn_nginx_servers do |t|
      t.integer :nginx_id, null:false
      t.integer :server_id, null:false
      t.timestamps
    end
  end
end
