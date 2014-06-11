class CreateEmailHosts < ActiveRecord::Migration
  def change
    create_table :email_hosts do |t|
      t.integer :client_id, null: false
      t.datetime :created, null: false
    end
  end
end
