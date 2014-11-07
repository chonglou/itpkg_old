class CreateEmailHosts < ActiveRecord::Migration
  def change
    create_table :email_hosts do |t|
      t.string :name, null:false
      t.string :encrypted_password, null:false
      t.string :encrypted_password_salt, null:false
      t.string :encrypted_password_iv, null:false
      t.string :ip, null:false
      t.integer :weight, null:false,default:0
      t.timestamps
    end
    add_index :email_hosts, :ip, unique:true
  end
end
