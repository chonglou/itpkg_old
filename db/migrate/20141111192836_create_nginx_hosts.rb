class CreateNginxHosts < ActiveRecord::Migration
  def change
    create_table :nginx_hosts do |t|

      t.timestamps
    end
  end
end
