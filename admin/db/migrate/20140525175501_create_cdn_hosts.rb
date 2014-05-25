class CreateCdnHosts < ActiveRecord::Migration
  def change
    create_table :cdn_hosts do |t|

      t.timestamps
    end
  end
end
