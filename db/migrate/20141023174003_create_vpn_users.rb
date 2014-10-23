class CreateVpnUsers < ActiveRecord::Migration
  def change
    create_table :vpn_users do |t|
      t.string :name, null: false, limit: 32
      t.string :passwd, null: false
      t.string :email, null: false
      t.boolean :enable, null:false,default:false
      t.date :start_date, null: false
      t.date :end_date, null: false
      t.timestamps
    end
    add_index :vpn_users, :name, unique: true
  end
end
